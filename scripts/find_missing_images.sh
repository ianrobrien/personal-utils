#!/bin/bash

# This script checks for missing PNG files in the "Imgs" subdirectory of each folder in the current directory.
# It looks for ZIP or CHD files and verifies if corresponding PNG files exist, listing any missing files.
# Usage: Run this script in the directory containing folders with associated 'Imgs' subdirectories.
# Output: Displays folders with missing PNG files and their respective filenames.
# Note: Ensure proper execution permissions are set for this script (chmod +x script_name.sh).

main() {
  echo "Finding missing images..."

  # Set base directory to the current directory
  base_dir="."

  # Loop through each folder in the base directory
  for folder in "$base_dir"/*; do
    if [ -d "$folder" ]; then
      folder_name=$(basename "$folder")
      missing_files=()

      # Check if "Imgs" subdirectory exists
      if [ -d "$folder/Imgs" ]; then
        # Loop through each ZIP or CHD file in the current folder
        for file in "$folder"/*.{zip,chd}; do
          if [ -f "$file" ]; then
            filename=$(basename "$file")
            filename_no_ext="${filename%.*}"

            # Check if corresponding PNG file exists in "Imgs"
            if [ ! -f "$folder/Imgs/$filename_no_ext.png" ]; then
              missing_files+=("$filename_no_ext")
            fi
          fi
        done

        # Output missing files for the current folder in the specified format
        if [ ${#missing_files[@]} -gt 0 ] && [ "${missing_files[0]}" = "NEOGEO" ]; then
          echo "$folder_name:"
          for file in "${missing_files[@]}"; do
            # Exclude "neogeo.zip" from the output
            if [ "$file" != "neogeo" ]; then
              echo "  $file"
            fi
          done
        fi
      fi
    fi
  done

  echo "Finished finding missing images."
}

main
