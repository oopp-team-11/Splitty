## Setup
- When setting up server and client run configurations in IntelliJ or any other IDE, it is advised
to set the working directories to client and server directories accordingly 
to comply with the working directories from ./gradlew bootRun and ./gradlew run.
- If you have cloned this repository and launched the application before the code freeze (14.04), 
please ensure that there are no old h2-database files in server or project directories.
These files are ignored by git and only saved locally.
## Keyboard shortcuts
### General
- esc for going to start screen and aborting changes
- enter for accepting changes
- tab and shift+tab for switching between UI elements
- space for clicking the selected UI element
- L to open the language menu
### Event overview
- E to add expense
- P to add participant
- T to change title
- I to click invite button
- ctrl + 0-9 to edit 1.-10. participant
- alt + 0-9 to delete 1.-10. participant
- shift + 0-9 to edit 1.-10. expense
- shift + ctrl + 0-9 to delete 1.-10. expense
### Start screen
- ctrl + 0-9 to join 1.-10. event on the start screen
- C to create event on the start screen
- J to join event on the start screen
### Admin panel
- ctrl + 0-9 to dump json of 1.-10. event
- alt + 0-9 to delete 1.-10. event
- I to import json

## Explanation of potentially unintuitive features
- Double-click on an expense to access a partial debt settling page (detailed expense overview)
- Partial debt settling page functionality (detailed expense overview)
    * Click for those participants whose debts are already settled
    * Press the "Save partial debts" (green button) to save changes for the partial debts of the specific expense
    * Press the "Go to Event Overview" (red button) to exit the page without saving the partial debts for specific expense
- Admin password is printed out on the server console on start up
- When using UI for changing the server URL, the server URL should contain both (and only) ip and a port number