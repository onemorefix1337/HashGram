import sys
from PIL import Image, ImageDraw

def create_icons(input_path):
    img = Image.open(input_path).convert("RGBA")
    
    sizes = {
        "mdpi": 48,
        "hdpi": 72,
        "xhdpi": 96,
        "xxhdpi": 144,
        "xxxhdpi": 192
    }
    
    for dpi, size in sizes.items():
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # 1. Standard ic_launcher.png (Rounded Square)
        # Usually Android icons have a slight rounding. Let's do 10% radius.
        radius = int(size * 0.15)
        square_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        mask = Image.new("L", (size, size), 0)
        draw = ImageDraw.Draw(mask)
        draw.rounded_rectangle((0, 0, size, size), radius=radius, fill=255)
        square_img.paste(resized, (0, 0), mask=mask)
        
        square_img.save(f"TMessagesProj/src/main/res/mipmap-{dpi}/ic_launcher.png", "PNG")
        
        # 2. Round ic_launcher_round.png (Circle)
        round_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        mask_round = Image.new("L", (size, size), 0)
        draw_round = ImageDraw.Draw(mask_round)
        draw_round.ellipse((0, 0, size, size), fill=255)
        round_img.paste(resized, (0, 0), mask=mask_round)
        
        round_img.save(f"TMessagesProj/src/main/res/mipmap-{dpi}/ic_launcher_round.png", "PNG")
        
        # TMessagesProj_AppStandalone
        try:
            square_img.save(f"TMessagesProj_AppStandalone/src/main/res/mipmap-{dpi}/ic_launcher_sa.png", "PNG")
            round_img.save(f"TMessagesProj_AppStandalone/src/main/res/mipmap-{dpi}/ic_launcher_round_sa.png", "PNG")
        except:
            pass

input_image = "/Users/user/.gemini/antigravity/brain/7e63d8a6-e078-4e99-8845-b85df4862c2e/hashgram_logo_material3_lavender_solid_1788549147382.jpg"
create_icons(input_image)
print("Icons rounded and saved.")
