import zlib
import struct
import os

def make_png(width, height, pixels):
    png_sig = b'\x89PNG\r\n\x1a\n'
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    ihdr_chunk = b'IHDR' + ihdr_data
    ihdr_chunk = struct.pack('>I', len(ihdr_data)) + ihdr_chunk + struct.pack('>I', zlib.crc32(ihdr_chunk))
    
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'
        for x in range(width):
            raw_data += bytes(pixels[y][x])
            
    idat_data = zlib.compress(raw_data)
    idat_chunk = b'IDAT' + idat_data
    idat_chunk = struct.pack('>I', len(idat_data)) + idat_chunk + struct.pack('>I', zlib.crc32(idat_chunk))
    
    iend_chunk = struct.pack('>I', 0) + b'IEND' + struct.pack('>I', zlib.crc32(b'IEND'))
    
    return png_sig + ihdr_chunk + idat_chunk + iend_chunk

width = 16
height = 16

grid = [
    "                ",
    "        #       ",
    "       # #      ",
    "      #   #     ",
    "     #  #  #    ",
    "     #  #  #    ",
    "     #  #  #    ",
    "      #  # #    ",
    "     # #  #     ",
    "     #  #  #    ",
    "     #  #  #    ",
    "     #  #  #    ",
    "      #   #     ",
    "       # #      ",
    "        #       ",
    "                ",
]

pixels = []
for row in grid:
    pixel_row = []
    for char in row:
        if char == '#':
            pixel_row.append((255, 255, 255, 255))
        else:
            pixel_row.append((0, 0, 0, 0))
    pixels.append(pixel_row)

png_content = make_png(width, height, pixels)

target_path = r'src/main/resources/assets/inventorysorter/textures/gui/sort_button.png'
os.makedirs(os.path.dirname(target_path), exist_ok=True)
with open(target_path, 'wb') as f:
    f.write(png_content)
print(f"Icon generated at {target_path}")
