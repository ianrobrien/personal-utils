#!/bin/bash

# rg35xx_resize.sh - Resize an image based on the specified gaming system.

# Usage:
#   ./rg35xx_resize.sh --system=<system> <source_image>
#
# Arguments:
#   --system=<system> - Specify the gaming system for which the image should be resized.
#                       Choose from ARCADE, FC, GB, GBA, GBC, GG, MD, NEOGEO, PS, SEGACD, or SFC.
#   <source_image>    - The source image file that you want to resize.
#
# Examples:
#   ./rg35xx_resize.sh --system=ARCADE image.png
#   ./rg35xx_resize.sh --system=FC input.jpg
#   ./rg35xx_resize.sh --system=SFC photo.png
#
# The script will resize the <source_image> to the specified dimensions of the chosen gaming system.
# The resized image will be saved with the "_resized.png" suffix in the same directory as the source image.
# If the output image is not in PNG format, it will be automatically converted to PNG.

main() {
  # Check if ImageMagick is installed
  if ! command -v convert &>/dev/null; then
    echo "ImageMagick is not installed. Please install it first."
    exit 1
  fi

  # Initialize variables
  source_image=""
  system=""

  # Parse command line arguments
  while [[ "$#" -gt 0 ]]; do
    case "$1" in
    --system=*)
      system="${1#*=}"
      ;;
    *)
      source_image="$1"
      ;;
    esac
    shift
  done

  # Check if the required arguments are provided
  if [ -z "$source_image" ] || [ -z "$system" ]; then
    echo "Usage: $0 --system=<system> <source_image>"
    exit 1
  fi

  case "$system" in
  ARCADE)
    dimensions="340x465"
    ;;
  FC)
    dimensions="340x465"
    ;;
  GB)
    dimensions="340x340"
    ;;
  GBA)
    dimensions="340x340"
    ;;
  GBC)
    dimensions="340x340"
    ;;
  GG)
    dimensions="340x466"
    ;;
  MD)
    dimensions="340x478"
    ;;
  NEOGEO)
    dimensions="340x466"
    ;;
  PS)
    dimensions="340x320"
    ;;
  SEGACD)
    dimensions="340x480"
    ;;
  SFC)
    dimensions="340x249"
    ;;
  *)
    echo "Invalid system argument. Choose from ARCADE, FC, GB, GBA, GBC, GG, MD, NEOGEO, PS, SEGACD, or SFC."
    exit 1
    ;;
  esac

  # Generate the output filename by appending "resized" to the source image name
  output_image="${source_image%.*}_resized.png"

  # Resize the source image to the specified dimensions
  convert "$source_image" -resize "$dimensions" -gravity west -background none -extent 640x480 "$output_image"

  # Check if the output image is not in PNG format and convert it to PNG if needed
  if [[ "$output_image" != *.png ]]; then
    converted_image="${output_image%.*}.png"
    mv "$output_image" "$converted_image"
    output_image="$converted_image"
  fi

  echo "Image processing completed for $system. Result saved as $output_image."
}

main "$@"
