import os
import re
import subprocess

UPLOADS_DIR = "/home/sonu/Desktop/spring_boot/alphagym/uploads"
MUSCLES_DIR = os.path.join(UPLOADS_DIR, "muscles")
EXERCISES_DIR = os.path.join(UPLOADS_DIR, "exercises")

# Get existing downloaded files
muscle_files = {os.path.splitext(f)[0].lower(): f for f in os.listdir(MUSCLES_DIR) if os.path.isfile(os.path.join(MUSCLES_DIR, f))}
exercise_files = {os.path.splitext(f)[0].lower(): f for f in os.listdir(EXERCISES_DIR) if os.path.isfile(os.path.join(EXERCISES_DIR, f))}

print(f">>> Found {len(muscle_files)} muscle diagram files and {len(exercise_files)} exercise media files in uploads!")

def get_muscle_img_for_category(category, muscle_name_hint=""):
    cat_lower = category.lower()
    hint_lower = muscle_name_hint.lower().replace(" ", "").replace("_", "")
    
    # 1. Direct hint match
    for m_k, m_f in muscle_files.items():
        clean_k = m_k.replace("_", "").replace("-", "")
        if hint_lower and (hint_lower in clean_k or clean_k in hint_lower):
            return f"/uploads/muscles/{m_f}"
            
    # 2. Category default mapping
    cat_muscle_map = {
        'chest': ['pectoral', 'chest', 'rectus'],
        'back': ['latissimus', 'trapezius', 'rhomboid', 'back'],
        'shoulder': ['deltoid', 'shoulder', 'trapezius'],
        'biceps': ['biceps', 'brachii', 'arm'],
        'triceps': ['triceps', 'brachii', 'arm'],
        'abs': ['rectus', 'abdominis', 'oblique', 'waist'],
        'quads': ['quadriceps', 'gluteus', 'hamstring', 'thigh'],
        'calves': ['gastrocnemius', 'soleus', 'calf', 'calves'],
        'forearms': ['brachioradialis', 'wrist', 'forearm'],
        'neck': ['sternocleidomastoid', 'splenius', 'neck']
    }
    
    keywords = cat_muscle_map.get(cat_lower, [])
    for kw in keywords:
        for m_k, m_f in muscle_files.items():
            if kw in m_k:
                return f"/uploads/muscles/{m_f}"
                
    if muscle_files:
        return f"/uploads/muscles/{list(muscle_files.values())[0]}"
    return None

workouts = []
seen_names = set()

for ex_slug, ex_file in exercise_files.items():
    # Convert slug to readable clean title
    title_words = ex_slug.replace('_', ' ').strip().split()
    clean_name = " ".join([w.capitalize() if not w.isupper() else w for w in title_words])
    
    # Fix common abbreviations
    clean_name = re.sub(r'\bBb\b', 'Barbell', clean_name)
    clean_name = re.sub(r'\bDb\b', 'Dumbbell', clean_name)
    clean_name = re.sub(r'\bCb\b', 'Cable', clean_name)
    clean_name = re.sub(r'\bBw\b', 'Bodyweight', clean_name)
    clean_name = re.sub(r'\bWt\b', 'Weighted', clean_name)
    clean_name = re.sub(r'\bAs\b', 'Assisted', clean_name)
    clean_name = re.sub(r'\bSm\b', 'Smith', clean_name)
    clean_name = re.sub(r'\bLv\b', 'Lever', clean_name)
    clean_name = re.sub(r'\bKb\b', 'Kettlebell', clean_name)
    clean_name = re.sub(r'\bMb\b', 'Medicine Ball', clean_name)
    clean_name = re.sub(r'\bStr\b', 'Stretch', clean_name)
    clean_name = re.sub(r'\bSusp\b', 'Suspended', clean_name)
    
    clean_name = re.sub(r'\s+', ' ', clean_name).strip()
    
    if len(clean_name) < 3 or clean_name.lower() in seen_names:
        continue
    seen_names.add(clean_name.lower())
    
    lower = clean_name.lower()
    
    # 1. Precise Category & Muscle Classification
    if any(k in lower for k in ['neck', 'sternocleidomastoid', 'splenius', 'cervicis']):
        category = 'Neck'
        target_muscles = 'Sternocleidomastoid, Splenius, Neck Extensors'
        hint = 'sternocleidomastoid'
    elif any(k in lower for k in ['calf', 'calves', 'soleus', 'gastrocnemius', 'shin', 'tibialis']):
        category = 'Calves'
        target_muscles = 'Gastrocnemius, Soleus, Tibialis Anterior'
        hint = 'gastrocnemius'
    elif any(k in lower for k in ['forearm', 'wrist', 'pronation', 'supination', 'grip', 'farmer', 'brachioradialis']):
        category = 'Forearms'
        target_muscles = 'Wrist Flexors, Wrist Extensors, Brachioradialis, Pronator Teres'
        hint = 'brachioradialis'
    elif any(k in lower for k in ['tricep', 'skull crusher', 'pushdown', 'bench dip', 'close grip push', 'kickback']):
        category = 'Triceps'
        target_muscles = 'Triceps Brachii (Lateral, Long & Medial Heads)'
        hint = 'tricepsbrachii'
    elif any(k in lower for k in ['bicep', 'curl', 'preacher', 'brachialis']):
        category = 'Biceps'
        target_muscles = 'Biceps Brachii (Short & Long Head), Brachialis'
        hint = 'bicepsbrachii'
    elif any(k in lower for k in ['chest', 'bench press', 'fly', 'pushup', 'push-up', 'pectoral', 'chest dip']):
        category = 'Chest'
        target_muscles = 'Pectoralis Major (Sternal & Clavicular Heads), Anterior Deltoid'
        hint = 'pectoralissternal'
    elif any(k in lower for k in ['shoulder', 'deltoid', 'delt', 'military press', 'overhead press', 'lateral raise', 'front raise', 'face pull', 'arnold press', 'upright row']):
        category = 'Shoulder'
        target_muscles = 'Anterior, Lateral & Posterior Deltoids, Supraspinatus'
        hint = 'deltoidanterior'
    elif any(k in lower for k in ['abs', 'abdom', 'crunch', 'plank', 'twist', 'oblique', 'core', 'sit-up', 'leg raise', 'waist', 'v-up']):
        category = 'Abs'
        target_muscles = 'Rectus Abdominis, Transverse Abdominis, Obliques'
        hint = 'rectusabdominis'
    elif any(k in lower for k in ['squat', 'lunge', 'leg press', 'leg extension', 'leg curl', 'glute', 'hamstring', 'quad', 'hip', 'step-up', 'box jump', 'thigh', 'hop', 'jump', 'shuffle', 'sprint', 'run', 'cycling']):
        category = 'Quads'
        target_muscles = 'Quadriceps, Gluteus Maximus, Hamstrings, Hip Abductors'
        hint = 'quadriceps'
    elif any(k in lower for k in ['pullup', 'pull-up', 'pulldown', 'row', 'lat', 'back', 'clean', 'snatch', 'deadlift', 'shrug', 'trapezius', 'rhomboid', 'erector spinae', 'hyperextension']):
        category = 'Back'
        target_muscles = 'Latissimus Dorsi, Trapezius, Rhomboids, Erector Spinae'
        hint = 'latissimusdorsi'
    else:
        category = 'Quads'
        target_muscles = 'Quadriceps, Core & Full Body Conditioning'
        hint = 'quadriceps'
        
    img_url = get_muscle_img_for_category(category, hint)
    video_url = f"/uploads/exercises/{ex_file}"
    
    # Difficulty
    if any(k in lower for k in ['barbell', 'olympic', 'snatch', 'clean', 'jerk', 'depth jump', 'muscle-up', 'weighted']):
        difficulty = 'Advanced'
    elif any(k in lower for k in ['dumbbell', 'cable', 'kettlebell', 'medicine ball', 'incline', 'decline', 'lever']):
        difficulty = 'Intermediate'
    else:
        difficulty = 'Beginner'
        
    description = f"Targeted exercise for {target_muscles}. Execute with controlled tempo, proper spinal alignment, and full range of motion."
    
    workouts.append({
        'name': clean_name,
        'description': description,
        'category': category,
        'difficulty': difficulty,
        'durationMinutes': 15,
        'targetMuscles': target_muscles,
        'imageUrl': img_url,
        'videoUrl': video_url
    })

print(f">>> Prepared {len(workouts)} fully populated workouts across 10 clean categories!")

sql_path = "/home/sonu/Desktop/spring_boot/alphagym/src/main/resources/workouts_seed.sql"
with open(sql_path, 'w', encoding='utf-8') as f:
    f.write("USE alpha;\n")
    f.write("DELETE FROM workouts;\n")
    for w in workouts:
        name_esc = w['name'].replace("'", "''")
        desc_esc = w['description'].replace("'", "''")
        cat_esc = w['category'].replace("'", "''")
        diff_esc = w['difficulty'].replace("'", "''")
        tm_esc = w['targetMuscles'].replace("'", "''")
        img_esc = f"'{w['imageUrl']}'" if w['imageUrl'] else "NULL"
        vid_esc = f"'{w['videoUrl']}'" if w['videoUrl'] else "NULL"
        dur = w['durationMinutes']
        f.write(f"INSERT INTO workouts (name, description, category, difficulty, duration_minutes, target_muscles, image_url, video_url, created_at) VALUES ('{name_esc}', '{desc_esc}', '{cat_esc}', '{diff_esc}', {dur}, '{tm_esc}', {img_esc}, {vid_esc}, NOW());\n")

print(">>> Loading into MySQL database alpha...")
subprocess.run(f"mysql -u root -p15061999 alpha < {sql_path}", shell=True, check=True)

print(">>> SUCCESS: All workouts accurately categorized and loaded in MySQL!")
