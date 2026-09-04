from PIL import Image, ImageOps
import glob
import os

# Source icon (the solid one)
src_path = "/Users/user/.gemini/antigravity/brain/7e63d8a6-e078-4e99-8845-b85df4862c2e/hashgram_logo_material3_lavender_solid_1788549147382.jpg"

try:
    img = Image.open(src_path).convert("RGBA")
    
    # We want to make a monochrome icon. Let's grayscale it.
    gray = img.convert("L")
    
    # Invert so the logo (which is probably lighter in the center) becomes the alpha mask
    # Actually, let's just use it as a mask, or threshold it.
    # The logo has a lavender background and a white H in the middle.
    # If we want the H to be solid white and background transparent:
    # white pixels -> alpha 255
    # colored pixels -> alpha 0
    
    # Let's write a simple thresholding:
    data = img.getdata()
    newData = []
    for item in data:
        # Check if the pixel is white (or very close to it)
        if item[0] > 240 and item[1] > 240 and item[2] > 240:
            newData.append((255, 255, 255, 255)) # White and opaque
        else:
            newData.append((255, 255, 255, 0)) # Transparent
            
    img.putdata(newData)
    
    # Resize to standard notification icon sizes
    sizes = {
        "mdpi": 24,
        "hdpi": 36,
        "xhdpi": 48,
        "xxhdpi": 72,
        "xxxhdpi": 96
    }
    
    for density, size in sizes.items():
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # Save to both TMessagesProj and TMessagesProj_AppStandalone
        for proj in ["TMessagesProj", "TMessagesProj_AppStandalone"]:
            path = f"{proj}/src/main/res/drawable-{density}/notification.png"
            if os.path.exists(os.path.dirname(path)):
                resized.save(path, "PNG")
                print(f"Saved {path}")
                
except Exception as e:
    print(f"Error: {e}")

