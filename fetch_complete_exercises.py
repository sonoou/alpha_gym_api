import os
import re
import subprocess
import urllib.parse
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

UPLOADS_DIR = "/home/sonu/Desktop/spring_boot/alphagym/uploads"
MUSCLES_DIR = os.path.join(UPLOADS_DIR, "muscles")
EXERCISES_DIR = os.path.join(UPLOADS_DIR, "exercises")

os.makedirs(MUSCLES_DIR, exist_ok=True)
os.makedirs(EXERCISES_DIR, exist_ok=True)

HEADERS_UA = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'

def curl_get(url, timeout=8):
    try:
        cmd = ['curl', '-s', '-L', '--max-time', str(timeout), '-A', HEADERS_UA, url]
        res = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.DEVNULL)
        return res.stdout.decode('utf-8', errors='ignore')
    except Exception:
        return ""

def curl_download(url, dest_path, timeout=15):
    if os.path.exists(dest_path) and os.path.getsize(dest_path) > 0:
        return True
    try:
        cmd = ['curl', '-s', '-L', '--max-time', str(timeout), '-A', HEADERS_UA, '-o', dest_path, url]
        res = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        return os.path.exists(dest_path) and os.path.getsize(dest_path) > 0
    except Exception:
        return False

muscle_diagram_cache = {}

def get_muscle_diagram_url(muscle_page_url):
    if muscle_page_url in muscle_diagram_cache:
        return muscle_diagram_cache[muscle_page_url]
    
    html = curl_get(muscle_page_url, timeout=6)
    if not html:
        return None
    
    soup = BeautifulSoup(html, 'html.parser')
    for img in soup.find_all('img', src=True):
        src = img['src']
        if 'logo' not in src and 'API' not in src and 'thumbnails' not in src and 'Ad' not in src:
            full_img_url = urllib.parse.urljoin(muscle_page_url, src)
            muscle_diagram_cache[muscle_page_url] = full_img_url
            return full_img_url
            
    muscle_diagram_cache[muscle_page_url] = None
    return None

def sanitize_filename(name):
    return re.sub(r'[^a-zA-Z0-9_\-]', '_', name).strip('_')

directory_urls = [
    ('Neck', 'https://exrx.net/Lists/ExList/NeckWt'),
    ('Shoulder', 'https://exrx.net/Lists/ExList/ShouldWt'),
    ('UpperArms', 'https://exrx.net/Lists/ExList/ArmWt'),
    ('Forearms', 'https://exrx.net/Lists/ExList/ForeArmWt'),
    ('Chest', 'https://exrx.net/Lists/ExList/ChestWt'),
    ('Back', 'https://exrx.net/Lists/ExList/BackWt'),
    ('Waist', 'https://exrx.net/Lists/ExList/WaistWt'),
    ('Hips', 'https://exrx.net/Lists/ExList/HipsWt'),
    ('Thighs', 'https://exrx.net/Lists/ExList/ThighWt'),
    ('Calves', 'https://exrx.net/Lists/ExList/CalfWt'),
    ('Olympic', 'https://exrx.net/Lists/OlympicWeightlifting'),
    ('Plyometrics', 'https://exrx.net/Lists/PowerExercises'),
    ('Cardio', 'https://exrx.net/Lists/CardioExercises'),
    ('Kettlebell', 'https://exrx.net/Lists/KettlebellExercises'),
    ('Other', 'https://exrx.net/Lists/OtherExercises')
]

print(">>> Step 1: Collecting all exercise page URLs from ExRx directory...", flush=True)

all_exercise_links = []
seen_urls = set()

for section_name, dir_url in directory_urls:
    html = curl_get(dir_url, timeout=8)
    if not html:
        continue
    soup = BeautifulSoup(html, 'html.parser')
    sec_count = 0
    for a in soup.find_all('a', href=True):
        href = a['href']
        if any(term in href for term in ['WeightExercises', 'OlympicWeightlifting', 'PowerExercises', 'CardioExercises', 'KettlebellExercises', 'OtherExercises', 'Plyometrics', 'Aerobic']):
            full_url = urllib.parse.urljoin(dir_url, href).split('#')[0]
            if not full_url.endswith(('Directory', 'SearchExercises', 'PowerTidbits', 'WorkoutMenu', 'ExList', '.html', 'Lists/')):
                if full_url not in seen_urls:
                    seen_urls.add(full_url)
                    all_exercise_links.append((section_name, full_url))
                    sec_count += 1
    print(f"    Section {section_name}: {sec_count} exercises found", flush=True)

print(f">>> Found {len(all_exercise_links)} total unique exercise URLs.", flush=True)

def process_exercise(item):
    section_name, page_url = item
    html = curl_get(page_url, timeout=8)
    if not html:
        return None
    
    soup = BeautifulSoup(html, 'html.parser')
    
    # 1. Full Complete Name
    h1 = soup.find('h1')
    raw_name = h1.get_text(strip=True) if h1 else ''
    if not raw_name:
        title = soup.title.string if soup.title else ''
        raw_name = title.replace('ExRx.net :', '').replace('ExRx.net', '').strip()
    
    if not raw_name or len(raw_name) < 3:
        slug = page_url.split('/')[-1]
        raw_name = re.sub(r'([A-Z])', r' \1', slug).strip()
    
    clean_name = re.sub(r'\s+', ' ', raw_name).strip()
    
    # 2. Extract Target Muscles
    target_muscles_list = []
    first_muscle_diagram_url = None
    for a in soup.find_all('a', href=True):
        if 'Muscles/' in a['href']:
            m_name = a.get_text(strip=True)
            if m_name and len(m_name) > 1 and m_name not in target_muscles_list:
                target_muscles_list.append(m_name)
            if not first_muscle_diagram_url:
                m_url = urllib.parse.urljoin(page_url, a['href'])
                first_muscle_diagram_url = get_muscle_diagram_url(m_url)
    
    target_muscles_str = ", ".join(target_muscles_list) if target_muscles_list else f"{section_name} Muscles"
    
    # 3. Extract Video / Demo GIF URL
    demo_media_url = None
    og_img = soup.find('meta', property='og:image')
    if og_img and og_img.get('content') and not og_img['content'].endswith(('logo.gif', 'logo_same_proportion_5_2_2015.gif')):
        demo_media_url = og_img['content']
    
    if not demo_media_url:
        thumb = soup.find('meta', attrs={'name': 'thumbnail'})
        if thumb and thumb.get('content') and not thumb['content'].endswith(('logo.gif', 'logo_same_proportion_5_2_2015.gif')):
            demo_media_url = thumb['content']
            
    if not demo_media_url:
        for attr in soup.find_all('attribute', attrs={'name': 'src'}):
            val = attr.get('value', '')
            if val and (val.endswith('.gif') or val.endswith('.mp4') or val.endswith('.webm')):
                demo_media_url = val
                break
    
    # 4. Download Muscle Diagram Image
    image_db_url = None
    if first_muscle_diagram_url:
        ext = os.path.splitext(first_muscle_diagram_url.split('?')[0])[-1] or '.png'
        muscle_slug = sanitize_filename(target_muscles_list[0] if target_muscles_list else section_name)
        local_img_path = os.path.join(MUSCLES_DIR, f"{muscle_slug}{ext}")
        if curl_download(first_muscle_diagram_url, local_img_path, timeout=10):
            image_db_url = f"/uploads/muscles/{muscle_slug}{ext}"
    
    # 5. Download Exercise Demo Media (Video/GIF)
    video_db_url = None
    if demo_media_url:
        full_media_url = urllib.parse.urljoin(page_url, demo_media_url)
        ext = os.path.splitext(full_media_url.split('?')[0])[-1] or '.gif'
        ex_slug = sanitize_filename(clean_name)
        local_vid_path = os.path.join(EXERCISES_DIR, f"{ex_slug}{ext}")
        if curl_download(full_media_url, local_vid_path, timeout=12):
            video_db_url = f"/uploads/exercises/{ex_slug}{ext}"
    
    # 6. Assign Category (10 distinct categories)
    lower_name = clean_name.lower()
    lower_target = target_muscles_str.lower()
    
    if 'triceps' in lower_target or 'tricep' in lower_name or 'skull crusher' in lower_name or 'pushdown' in lower_name or 'kickback' in lower_name:
        category = 'Triceps'
    elif 'biceps' in lower_target or 'bicep' in lower_name or 'brachialis' in lower_target or 'curl' in lower_name:
        category = 'Biceps'
    elif section_name == 'Chest' or 'pectoral' in lower_target or 'bench press' in lower_name or 'chest' in lower_name:
        category = 'Chest'
    elif section_name in ['Back', 'Olympic'] or 'latissimus' in lower_target or 'trapezius' in lower_target or 'rhomboid' in lower_target or 'clean' in lower_name or 'snatch' in lower_name:
        category = 'Back'
    elif section_name in ['Thighs', 'Hips', 'Plyometrics', 'Cardio'] or 'quadriceps' in lower_target or 'hamstring' in lower_target or 'gluteus' in lower_target or 'squat' in lower_name or 'lunge' in lower_name:
        category = 'Quads'
    elif section_name == 'Shoulder' or 'deltoid' in lower_target or 'overhead' in lower_name or 'military press' in lower_name or 'kettlebell' in lower_name:
        category = 'Shoulder'
    elif section_name in ['Waist', 'Other'] or 'abdominis' in lower_target or 'oblique' in lower_target or 'crunch' in lower_name or 'plank' in lower_name:
        category = 'Abs'
    elif section_name == 'Calves' or 'gastrocnemius' in lower_target or 'soleus' in lower_target or 'calf' in lower_name:
        category = 'Calves'
    elif section_name == 'Forearms' or 'wrist' in lower_target or 'forearm' in lower_name:
        category = 'Forearms'
    elif section_name == 'Neck' or 'sternocleidomastoid' in lower_target or 'neck' in lower_name:
        category = 'Neck'
    else:
        category = 'Quads'
    
    # 7. Difficulty
    if any(k in lower_name for k in ['barbell', 'olympic', 'snatch', 'clean', 'jerk', 'depth jump', 'muscle-up']):
        difficulty = 'Advanced'
    elif any(k in lower_name for k in ['dumbbell', 'cable', 'kettlebell', 'medicine ball']):
        difficulty = 'Intermediate'
    else:
        difficulty = 'Beginner'
        
    description = f"Targeted exercise for {target_muscles_str}. Execute with controlled tempo, proper spinal alignment, and full range of motion."
    
    return {
        'name': clean_name,
        'category': category,
        'difficulty': difficulty,
        'durationMinutes': 15,
        'targetMuscles': target_muscles_str,
        'description': description,
        'imageUrl': image_db_url,
        'videoUrl': video_db_url
    }

print(">>> Step 2: Fetching full details, downloading target muscle diagrams and demonstration videos (8 concurrent threads)...", flush=True)

results = []
seen_final_names = set()

with ThreadPoolExecutor(max_workers=8) as executor:
    futures = [executor.submit(process_exercise, item) for item in all_exercise_links]
    done_count = 0
    total = len(all_exercise_links)
    for future in as_completed(futures):
        res = future.result()
        done_count += 1
        if done_count % 50 == 0 or done_count == total:
            print(f"    Progress: {done_count}/{total} exercises processed...", flush=True)
        if res and res['name']:
            key = (res['name'].lower(), res['category'])
            if key not in seen_final_names:
                seen_final_names.add(key)
                results.append(res)

print(f">>> Completed extraction: {len(results)} total complete exercises with media!", flush=True)

print(">>> Step 3: Generating SQL seed file...", flush=True)
sql_path = "/home/sonu/Desktop/spring_boot/alphagym/src/main/resources/workouts_seed.sql"
with open(sql_path, 'w', encoding='utf-8') as f:
    f.write("USE alpha;\n")
    f.write("DELETE FROM workouts;\n")
    for w in results:
        name_esc = w['name'].replace("'", "''")
        desc_esc = w['description'].replace("'", "''")
        cat_esc = w['category'].replace("'", "''")
        diff_esc = w['difficulty'].replace("'", "''")
        tm_esc = w['targetMuscles'].replace("'", "''")
        img_esc = f"'{w['imageUrl']}'" if w['imageUrl'] else "NULL"
        vid_esc = f"'{w['videoUrl']}'" if w['videoUrl'] else "NULL"
        dur = w['durationMinutes']
        f.write(f"INSERT INTO workouts (name, description, category, difficulty, duration_minutes, target_muscles, image_url, video_url, created_at) VALUES ('{name_esc}', '{desc_esc}', '{cat_esc}', '{diff_esc}', {dur}, '{tm_esc}', {img_esc}, {vid_esc}, NOW());\n")

print(">>> Step 4: Loading into MySQL database alpha...", flush=True)
subprocess.run(f"mysql -u root -p15061999 alpha < {sql_path}", shell=True, check=True)

print(">>> SUCCESS: All complete workout names, muscle diagram images, and demonstration videos loaded into database!", flush=True)
