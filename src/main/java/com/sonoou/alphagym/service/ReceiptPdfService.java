package com.sonoou.alphagym.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.entity.PaymentTransactionEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import com.sonoou.alphagym.repository.PaymentTransactionRepository;
import com.sonoou.alphagym.repository.UserRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptPdfService {

    private final PaymentTransactionRepository transactionRepository;
    private final MembershipPlanRepository planRepository;
    private final UserRepository userRepository;

    public ReceiptPdfService(PaymentTransactionRepository transactionRepository,
                             MembershipPlanRepository planRepository,
                             UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    public byte[] generateReceiptPdf(Long transactionId, String currentUserEmail) {
        PaymentTransactionEntity tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment Transaction not found with ID: " + transactionId));

        UserEntity user = tx.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Transaction does not have an associated user.");
        }

        if (currentUserEmail != null && !currentUserEmail.equalsIgnoreCase(user.getEmail())) {
            throw new SecurityException("You are not authorized to download this receipt.");
        }

        return buildPdf(tx, user);
    }

    public byte[] generateReceiptPdfByOrderId(String orderId, String currentUserEmail) {
        PaymentTransactionEntity tx = transactionRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment Transaction not found with Order ID: " + orderId));

        UserEntity user = tx.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Transaction does not have an associated user.");
        }

        if (currentUserEmail != null && !currentUserEmail.equalsIgnoreCase(user.getEmail())) {
            throw new SecurityException("You are not authorized to download this receipt.");
        }

        return buildPdf(tx, user);
    }

    private byte[] buildPdf(PaymentTransactionEntity tx, UserEntity user) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Receipt Slip Dimensions (approx 620 x 400 - Landscape Slip)
            Rectangle pageSize = new Rectangle(620, 400);
            Document document = new Document(pageSize, 25, 25, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Add Border Event to draw outer double-line rectangle
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document doc) {
                    PdfContentByte cb = writer.getDirectContent();
                    cb.setColorStroke(new Color(30, 30, 30));
                    cb.setLineWidth(1.8f);
                    // Outer border with soft rounded corners
                    cb.roundRectangle(16, 16, doc.getPageSize().getWidth() - 32, doc.getPageSize().getHeight() - 32, 10);
                    cb.stroke();
                }
            });

            document.open();

            // Load Bundled Fonts
            BaseFont devanagariBase = getDevanagariBaseFont();
            BaseFont helveticaBase = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            BaseFont helveticaBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            BaseFont helveticaOblique = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            BaseFont helveticaBoldOblique = BaseFont.createFont(BaseFont.HELVETICA_BOLDOBLIQUE, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);

            Font gymTitleFont = new Font(helveticaBoldOblique, 22, Font.NORMAL, new Color(25, 25, 25));
            Font addressFont = new Font(helveticaBase, 9f, Font.NORMAL, new Color(40, 40, 40));
            Font boldLabelFont = new Font(helveticaBold, 10f, Font.NORMAL, new Color(30, 30, 30));
            Font normalFont = new Font(helveticaBase, 9.5f, Font.NORMAL, new Color(30, 30, 30));
            Font scriptFont = new Font(helveticaBoldOblique, 12f, Font.NORMAL, new Color(15, 45, 120));
            Font smallFont = new Font(helveticaBase, 8.5f, Font.NORMAL, new Color(50, 50, 50));

            Font hindiHeadFont = (devanagariBase != null) ? new Font(devanagariBase, 9f, Font.BOLD, new Color(20, 20, 20)) : boldLabelFont;
            Font hindiBodyFont = (devanagariBase != null) ? new Font(devanagariBase, 8f, Font.NORMAL, new Color(40, 40, 40)) : smallFont;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate txDate = tx.getCreatedAt() != null ? tx.getCreatedAt().toLocalDate() : LocalDate.now();

            int durationMonths = 1;
            String planDurationLabel = "1 Month";
            if (tx.getPlanId() != null) {
                MembershipPlanEntity plan = planRepository.findById(tx.getPlanId()).orElse(null);
                if (plan != null && plan.getDurationMonths() != null) {
                    durationMonths = plan.getDurationMonths();
                    planDurationLabel = durationMonths + (durationMonths > 1 ? " Months" : " Month");
                }
            }
            LocalDate dueDate = txDate.plusMonths(durationMonths);

            // ================= HEADER TABLE =================
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{22f, 52f, 26f});

            // 1. Header Left: Logo / AV GFP + Receipt No
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            Paragraph logoP = new Paragraph("AV\nGFP", new Font(helveticaBold, 14, Font.NORMAL, new Color(30, 30, 30)));
            logoP.setAlignment(Element.ALIGN_LEFT);
            Paragraph subLogo = new Paragraph("Alpha Veins Gym", new Font(helveticaBase, 7.5f, Font.NORMAL, new Color(80, 80, 80)));
            subLogo.setAlignment(Element.ALIGN_LEFT);
            String receiptNumber = String.format("%04d", tx.getId());
            Paragraph recNoP = new Paragraph("\nNo.: " + receiptNumber, new Font(helveticaBold, 11, Font.NORMAL, new Color(30, 30, 30)));
            leftCell.addElement(logoP);
            leftCell.addElement(subLogo);
            leftCell.addElement(recNoP);
            headerTable.addCell(leftCell);

            // 2. Header Center: Alpha Veins Gym & Address
            PdfPCell centerCell = new PdfPCell();
            centerCell.setBorder(Rectangle.NO_BORDER);
            centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph gymTitle = new Paragraph("Alpha Veins Gym", gymTitleFont);
            gymTitle.setAlignment(Element.ALIGN_CENTER);
            Paragraph addressP1 = new Paragraph("H.No. 291/1, Kh.No. 1068, IInd Floor,", addressFont);
            addressP1.setAlignment(Element.ALIGN_CENTER);
            Paragraph addressP2 = new Paragraph("Village Bhalswa, Delhi-110033", addressFont);
            addressP2.setAlignment(Element.ALIGN_CENTER);
            centerCell.addElement(gymTitle);
            centerCell.addElement(addressP1);
            centerCell.addElement(addressP2);
            headerTable.addCell(centerCell);

            // 3. Header Right: Contacts & Dates
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph mobP = new Paragraph("Mob. No. : 9971241014", new Font(helveticaBold, 8.5f, Font.NORMAL, new Color(40, 40, 40)));
            mobP.setAlignment(Element.ALIGN_RIGHT);
            Paragraph waP = new Paragraph("WhatsApp: 9643055173", new Font(helveticaBase, 8.5f, Font.NORMAL, new Color(40, 40, 40)));
            waP.setAlignment(Element.ALIGN_RIGHT);
            Paragraph dateP = new Paragraph("Date : " + txDate.format(dtf), boldLabelFont);
            dateP.setAlignment(Element.ALIGN_RIGHT);
            Paragraph dueDateP = new Paragraph("Due Date : " + dueDate.format(dtf), boldLabelFont);
            dueDateP.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(mobP);
            rightCell.addElement(waP);
            rightCell.addElement(dateP);
            rightCell.addElement(dueDateP);
            headerTable.addCell(rightCell);

            document.add(headerTable);

            // Spacer
            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingBefore(3f);
            spacer.setSpacingAfter(3f);
            document.add(spacer);

            // ================= BODY DETAILS =================
            String memberDisplayName = user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getEmail();
            String planDisplayName = (tx.getPlanName() != null ? tx.getPlanName() : "Gym Membership Pass") + " (" + planDurationLabel + ")";

            // Line 1: Received with thanks from
            PdfPTable line1Table = new PdfPTable(2);
            line1Table.setWidthPercentage(100);
            line1Table.setWidths(new float[]{30f, 70f});
            PdfPCell l1Label = new PdfPCell(new Phrase("Received with thanks from : ", boldLabelFont));
            l1Label.setBorder(Rectangle.NO_BORDER);
            PdfPCell l1Val = new PdfPCell(new Phrase(memberDisplayName, scriptFont));
            l1Val.setBorder(Rectangle.BOTTOM);
            l1Val.setBorderWidthBottom(1f);
            line1Table.addCell(l1Label);
            line1Table.addCell(l1Val);
            document.add(line1Table);

            // Line 2: for the month of
            PdfPTable line2Table = new PdfPTable(2);
            line2Table.setWidthPercentage(100);
            line2Table.setWidths(new float[]{23f, 77f});
            PdfPCell l2Label = new PdfPCell(new Phrase("for the month of : ", boldLabelFont));
            l2Label.setBorder(Rectangle.NO_BORDER);
            PdfPCell l2Val = new PdfPCell(new Phrase(planDisplayName, scriptFont));
            l2Val.setBorder(Rectangle.BOTTOM);
            l2Val.setBorderWidthBottom(1f);
            line2Table.addCell(l2Label);
            line2Table.addCell(l2Val);
            document.add(line2Table);

            // Line 3: Payment details (Cash / Online)
            PdfPTable line3Table = new PdfPTable(4);
            line3Table.setWidthPercentage(100);
            line3Table.setWidths(new float[]{10f, 25f, 12f, 53f});

            Double amount = tx.getAmount() != null ? tx.getAmount() : 0.0;
            String paymentId = tx.getRazorpayPaymentId() != null ? tx.getRazorpayPaymentId() : "Razorpay (" + tx.getStatus() + ")";

            PdfPCell l3CashLabel = new PdfPCell(new Phrase("Cash : ", boldLabelFont));
            l3CashLabel.setBorder(Rectangle.NO_BORDER);
            PdfPCell l3CashVal = new PdfPCell(new Phrase("-", normalFont));
            l3CashVal.setBorder(Rectangle.BOTTOM);

            PdfPCell l3OnlineLabel = new PdfPCell(new Phrase("Online : ", boldLabelFont));
            l3OnlineLabel.setBorder(Rectangle.NO_BORDER);
            PdfPCell l3OnlineVal = new PdfPCell(new Phrase("Rs. " + String.format("%.0f", amount) + " (" + paymentId + ")", scriptFont));
            l3OnlineVal.setBorder(Rectangle.BOTTOM);

            line3Table.addCell(l3CashLabel);
            line3Table.addCell(l3CashVal);
            line3Table.addCell(l3OnlineLabel);
            line3Table.addCell(l3OnlineVal);
            document.add(line3Table);

            // Line 4: Highlighted Receipt Amount Box
            PdfPTable amountRow = new PdfPTable(2);
            amountRow.setWidthPercentage(100);
            amountRow.setWidths(new float[]{38f, 62f});

            PdfPCell amountBoxCell = new PdfPCell();
            amountBoxCell.setBorder(Rectangle.BOX);
            amountBoxCell.setBorderWidth(1.5f);
            amountBoxCell.setPadding(6);
            amountBoxCell.setBackgroundColor(new Color(245, 248, 252));
            Paragraph amountP = new Paragraph("Receipt   Rs. " + String.format("%.0f", amount) + "/-", new Font(helveticaBold, 13, Font.NORMAL, new Color(20, 20, 20)));
            amountBoxCell.addElement(amountP);

            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);

            amountRow.addCell(amountBoxCell);
            amountRow.addCell(emptyCell);
            document.add(amountRow);

            // Spacer
            Paragraph spacer2 = new Paragraph(" ");
            spacer2.setSpacingBefore(2f);
            document.add(spacer2);

            // Line 5: Notice & Signature Footer
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{65f, 35f});

            // Footer Left: Note & Hindi Notice
            PdfPCell notesCell = new PdfPCell();
            notesCell.setBorder(Rectangle.NO_BORDER);
            Paragraph noteP = new Paragraph("Note : We Also Provide Personal Training And Customised Diet Chart*", new Font(helveticaBold, 8f, Font.NORMAL, new Color(30, 30, 30)));
            Paragraph noticeHead = new Paragraph("आवश्यक सूचना :", hindiHeadFont);
            Paragraph noticeBody1 = new Paragraph("• जीम के अन्दर कोई भी कीमती वस्तु जैसे मोबाईल, घड़ी, पर्स आदि का स्वयं ध्यान रखें।", hindiBodyFont);
            Paragraph noticeBody2 = new Paragraph("• किसी भी प्रकार के नुकसान की जिम्मेदारी प्रबंधक की नहीं होगी।", hindiBodyFont);

            notesCell.addElement(noteP);
            notesCell.addElement(noticeHead);
            notesCell.addElement(noticeBody1);
            notesCell.addElement(noticeBody2);
            footerTable.addCell(notesCell);

            // Footer Right: For Alpha Veins Gym & Signature
            PdfPCell signCell = new PdfPCell();
            signCell.setBorder(Rectangle.NO_BORDER);
            signCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph forGym = new Paragraph("For Alpha Veins Gym", new Font(helveticaBoldOblique, 10.5f, Font.NORMAL, new Color(20, 20, 20)));
            forGym.setAlignment(Element.ALIGN_RIGHT);
            Paragraph signLine = new Paragraph("\nAuthorized Signatory", new Font(helveticaBold, 8.5f, Font.NORMAL, new Color(40, 40, 40)));
            signLine.setAlignment(Element.ALIGN_RIGHT);
            Paragraph signatureLabel = new Paragraph("Signature", new Font(helveticaOblique, 8f, Font.NORMAL, new Color(80, 80, 80)));
            signatureLabel.setAlignment(Element.ALIGN_RIGHT);

            signCell.addElement(forGym);
            signCell.addElement(signLine);
            signCell.addElement(signatureLabel);
            footerTable.addCell(signCell);

            document.add(footerTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate receipt PDF: " + e.getMessage(), e);
        }
    }

    private BaseFont getDevanagariBaseFont() {
        try {
            ClassPathResource fontResource = new ClassPathResource("fonts/NotoSansDevanagari-Regular.ttf");
            if (fontResource.exists()) {
                try (InputStream is = fontResource.getInputStream()) {
                    byte[] fontBytes = is.readAllBytes();
                    return BaseFont.createFont("NotoSansDevanagari-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load Devanagari font: " + e.getMessage());
        }
        return null;
    }
}
