#!/usr/bin/env python3
"""
Generate Android app icons from source image.
Requires: pip install pillow
"""

from PIL import Image
import os
import sys

def generate_icons(source_path, output_dir="app/src/main/res"):
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
    
    # Round icon sizes (same as regular but with rounded corners mask)
    round_icon_sizes = icon_sizes.copy()
    
    try:
        # Open source image
        img = Image.open(source_path)
        print(f"Opened source image: {img.size} {img.mode}")
        
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
        for density, size in round_icon_sizes.items():
            icon = img.resize((size, size), Image.Resampling.LANCZOS)
            
            # Create circular mask
            mask = Image.new('L', (size, size), 0)
            from PIL import ImageDraw
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
        # For adaptive icons, foreground should be 108x108dp with safe zone 66x66dp
        adaptive_size = 432  # xxxhdpi
        foreground_size = 288  # 66dp * 4 (xxxhdpi) - content within safe zone
        adaptive_icon = img.resize((foreground_size, foreground_size), Image.Resampling.LANCZOS)
        
        # Create 432x432 canvas with transparent background
        adaptive_canvas = Image.new('RGBA', (adaptive_size, adaptive_size), (0, 0, 0, 0))
        # Center the foreground
        offset = (adaptive_size - foreground_size) // 2
        adaptive_canvas.paste(adaptive_icon, (offset, offset), adaptive_icon)
        
        # Save adaptive icon foreground for each density
        adaptive_sizes = {
            "mipmap-mdpi": 108,
            "mipmap-hdpi": 162,
            "mipmap-xhdpi": 216,
            "mipmap-xxhdpi": 324,
            "mipmap-xxxhdpi": 432,
        }
        
        for density, size in adaptive_sizes.items():
            fg_size = size * 66 // 108  # 66dp safe zone
            canvas_size = size
            fg = img.resize((fg_size, fg_size), Image.Resampling.LANCZOS)
            canvas = Image.new('RGBA', (canvas_size, canvas_size), (0, 0, 0, 0))
            offset = (canvas_size - fg_size) // 2
            canvas.paste(fg, (offset, offset), fg)
            
            output_path = os.path.join(output_dir, density, "ic_launcher_foreground.png")
            canvas.save(output_path, "PNG")
            print(f"Generated adaptive foreground: {output_path} ({size}x{size})")
        
        print("\n✅ All icons generated successfully!")
        print(f"\n📁 Icons saved to: {output_dir}")
        print("\n📋 Next steps:")
        print("1. Add background layer for adaptive icons (ic_launcher_background.xml)")
        print("2. Update mipmap-anydpi-v26/ic_launcher.xml and ic_launcher_round.xml")
        print("3. Test on device/emulator")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    source = r"D:\WEBSITES\pop chat (andorid app)\pop no name.jpg"
    if not os.path.exists(source):
        print(f"❌ Source image not found: {source}")
        sys.exit(1)
    
    generate_icons(source)