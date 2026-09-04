import os
import re

ROOT = "/Users/user/HashGram"

def replace_in_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        return False
        
    new_content = content
    # Replace exact matches
    new_content = new_content.replace("HashGram", "HashGram")
    new_content = new_content.replace("hashgram", "hashgram")
    new_content = new_content.replace("HASHGRAM", "HASHGRAM")
    new_content = new_content.replace("Hashgram", "Hashgram")
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {filepath}")
        return True
    return False

# Files to skip (like .git, .gradle, build folders etc.)
SKIP_DIRS = {'.git', '.gradle', 'build', 'captures', '.idea'}

def walk_and_replace(directory):
    for root, dirs, files in os.walk(directory):
        dirs[:] = [d for d in dirs if d not in SKIP_DIRS and not d.startswith('build')]
        for file in files:
            # Only process known text files or skip binaries
            if file.endswith(('.png', '.jpg', '.jpeg', '.webp', '.so', '.aar', '.jar', '.apk', '.class', '.dex')):
                continue
            filepath = os.path.join(root, file)
            replace_in_file(filepath)

def rename_files(directory):
    for root, dirs, files in os.walk(directory, topdown=False):
        dirs[:] = [d for d in dirs if d not in SKIP_DIRS and not d.startswith('build')]
        for file in files:
            if "HashGram" in file:
                old_path = os.path.join(root, file)
                new_file = file.replace("HashGram", "HashGram")
                new_path = os.path.join(root, new_file)
                os.rename(old_path, new_path)
                print(f"Renamed {old_path} -> {new_path}")

if __name__ == "__main__":
    print("Replacing text...")
    walk_and_replace(ROOT)
    print("Renaming files...")
    rename_files(ROOT)
