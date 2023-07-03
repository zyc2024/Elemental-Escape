# Elemental-Escape

## Requirements
- [Intellij](https://www.jetbrains.com/idea/) - the Community Edition is free to download. You will need approximately 3.5GB hard disk space for Intellij application.
- [Java 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or newer versions such as [Java 17](https://www.oracle.com/java/technologies/downloads/#java17). It is preferred to use LTS versions (Java 8, 11, or 17) and also best for development if everyone uses the same version. There are new syntatic features introduced in post java-11 versions such as pattern matching/switch expressions. You can open a command prompt or terminal to check java version.
    - Mac/Linux (terminal): $ java --version
    - Windows (Command Prompt): 
- [Tiled](https://thorbjorn.itch.io/tiled) will be our level editor. The download page may ask for a donation or payment but it's free. This is a very powerful level editor with many features for creative level design.

## Getting Started

### Mac Instructions

1. Open up Intellij and You should see these following options
    - New Project: create fresh project (this does not provide LIBGDX settings)
    - Open: loading project on disk
    - Get from VCS: pick this one
2. Selecting "Get from VCS", choose "Repository URL" with Git as the version control. Paste the github clone URL and finish by clicking "clone" in the Intellij panel.
3. Wait for Intellij to finish some setups.
4. Go to File > Project Structure > Choose SDK for correct Java version (11). Select 11 as Language Level. Apply these changes and exit with "OK".
5. Create new configuration by finding "Run" on menu bar and choosing "Edit Configurations". Add a new run configuration. Input the following
    - Name: elemental-escape (anything works)
    - module: find the SDK (11, 17, etc).
    - cp: Elemental-Escape.desktop.main
    - Main class: com.elements.game.DesktopLauncher
    - Working Directory: click on the folder icon and find the folder "Elemental-Escape/assets" by first finding Elemental-Escape on your drive.

### Windows Instructions
1. to be added, it should be very similar to Mac (or same ?) I need to verify on a windows.

## Development Setup
Please see the `ProjectConventions.xml` file in the root folder. To enable consistent formatting,
you need to import this file in your project preferences.
1. Open preferences
2. Find and open Code Style
3. Click on Scheme dropdown and Make sure "Project" is selected and not default (unless you 
   prefer these settings to apply everywhere).
4. Click on Setting Gear icon dropdown next to Scheme dropdown
5. Import scheme and find the `ProjectConventions.xml` file.
6. Apply changes and Click 'OK' to exit.

## Contributors
- Zhiyuan Chen (Programmer, Lead)
- Wilson Zhang (Programmer, Designer)
- Bairu Li (Programmer, Designer)
- Terry Xu (Artist, Designer)