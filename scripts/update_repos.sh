#!/bin/bash

main() {
    # Navigate to the parent directory
    parent_dir=$(pwd)

    # Loop through each subdirectory
    for dir in "$parent_dir"/*; do
        if [ -d "$dir" ]; then
            echo "Entering $dir"
            cd "$dir" || continue
            # Run git pull and git tidy in each subdirectory
            git pull
            git tidy
            cd "$parent_dir" || exit
        fi
    done
}

main "$@"
