#!/usr/bin/env python3
"""
Generate Android app icons from source image.
Requires: pip install pillow
"""

from PIL import Image, ImageDraw
import os
import sys

def generate_icons(source_path, output_dir="app/src/main/res", logo_path=None):
    """Generate all required Android icon densities."""
    
    # Android icon sizes (density -> size in pixels)
    icon_sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    
    # Also generate play store icon (512x512)
    play_store_size = 512
    
    try:
        # Open source image for app icon
        img = Image.open(source_path)
        print(f"Opened source icon: {img.size} {img.mode}")
        
        # Convert to RGBA if not already
        if img.mode != 'RGBA':
            img = img.convert('RGBA')
        
        # Create a square version by cropping to center
        width, height = img.size
        size = min(width, height)
        left = (width - size) // 2
        top = (height - size) // 2
        img = img.crop((left, top, left + size, top + size))
        print(f"Cropped to square: {img.size}")
        
        # Create output directories
        for density in icon_sizes.keys():
            os.makedirs(os.path.join(output_dir, density), exist_ok=True)
        
        # Generate regular icons
        for density, size in icon_sizes.items():
            icon = img.resize((size, size), Image.Resampling.LANCZOS)
            output_path = os.path.join(output_dir, density, "ic_launcher.png")
            icon.save(output_path, "PNG")
            print(f"Generated: {output_path} ({size}x{size})")
        
        # Generate round icons (with circular mask)
        for density, size in icon_sizes.items():
            icon = img.resize((size, size), Image.Resampling.LANCZOS)
            
            # Create circular mask
            mask = Image.new('L', (size, size), 0)
            draw = ImageDraw.Draw(mask)
            draw.ellipse((0, 0, size, size), fill=255)
            
            # Apply mask
            icon.putalpha(mask)
            output_path = os.path.join(output_dir, density, "ic_launcher_round.png")
            icon.save(output_path, "PNG")
            print(f"Generated round: {output_path} ({size}x{size})")
        
        # Generate Play Store icon (512x512)
        play_store_icon = img.resize((play_store_size, play_store_size), Image.Resampling.LANCZOS)
        play_store_dir = os.path.join(output_dir, "..", "play_store")
        os.makedirs(play_store_dir, exist_ok=True)
        play_store_path = os.path.join(play_store_dir, "ic_launcher_play_store.png")
        play_store_icon.save(play_store_path, "PNG")
        print(f"Generated Play Store: {play_store_path} ({play_store_size}x{play_store_size})")
        
        # Generate adaptive icon foreground (108dp = 432px for xxxhdpi)
        adaptive_sizes = {
            "mipmap-mdpi": 108,
            "mipmap-hdpi": 162,
            "mipmap-xhdpi": 216,
            "mipmap-xxhdpi": 324,
            "mipmap-xxxhdpi": 432,
        }
        
        for density, size in adaptive_sizes.items():
            # Safe zone is 66dp (66/108 = 61% of total)
            fg_size = int(size * 66 / 108)
            canvas_size = size
            fg = img.resize((fg_size, fg_size), Image.Resampling.LANCZOS)
            canvas = Image.new('RGBA', (canvas_size, canvas_size), (0, 0, 0, 0))
            offset = (canvas_size - fg_size) // 2
            canvas.paste(fg, (offset, offset), fg)
            
            output_path = os.path.join(output_dir, density, "ic_launcher_foreground.png")
            canvas.save(output_path, "PNG")
            print(f"Generated adaptive foreground: {output_path} ({size}x{size})")
        
        # Generate adaptive icon background (solid color from theme)
        # Using the app's primary blue color
        background_color = (0x1E, 0x88, 0xE5, 0xFF)  # #1E88E5
        for density, size in adaptive_sizes.items():
            bg = Image.new('RGBA', (size, size), background_color)
            output_path = os.path.join(output_dir, density, "ic_launcher_background.png")
            bg.save(output_path, "PNG")
            print(f"Generated adaptive background: {output_path} ({size}x{size})")
        
        # Generate splash/logo from omi chat.png if provided
        if logo_path and os.path.exists(logo_path):
            logo_img = Image.open(logo_path)
            if logo_img.mode != 'RGBA':
                logo_img = logo_img.convert('RGBA')
            
            # Generate splash for different densities
            splash_sizes = {
                "drawable-mdpi": 320,
                "drawable-hdpi": 480,
                "drawable-xhdpi": 640,
                "drawable-xxhdpi": 960,
                "drawable-xxxhdpi": 1280,
            }
            
            for density, size in splash_sizes.items():
                # Maintain aspect ratio, fit within square
                logo_copy = logo_img.copy()
                logo_copy.thumbnail((size, size), Image.Resampling.LANCZOS)
                
                # Create square canvas with transparent background
                canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
                offset_x = (size - logo_copy.width) // 2
                offset_y = (size - logo_copy.height) // 2
                canvas.paste(logo_copy, (offset_x, offset_y), logo_copy)
                
                splash_dir = os.path.join(output_dir, density.replace("mipmap", "drawable"))
                os.makedirs(splash_dir, exist_ok=True)
                output_path = os.path.join(splash_dir, "splash_logo.png")
                canvas.save(output_path, "PNG")
                print(f"Generated splash: {output_path} ({size}x{size})")
        
        print("\n✅ All icons generated successfully!")
        print(f"\n📁 Icons saved to: {output_dir}")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    source = r"D:\WEBSITES\pop chat (andorid app)\pop no name.jpg"
    logo = r"D:\WEBSITES\pop chat (andorid app)\omi chat.png"
    
    if not os.path.exists(source):
        print(f"❌ Source icon not found: {source}")
        sys.exit(1)
    
    generate_icons(source, logo_path=logo)