#!/bin/bash

main() {
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
        if [ ${#missing_files[@]} -gt 0 ]; then
          echo "$folder_name:"
          for file in "${missing_files[@]}"; do
            echo "  $file"
          done
        fi
      fi
    fi
  done
}

main
