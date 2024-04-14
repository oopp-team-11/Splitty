# Product Pitch Draft

## Main block: The current state of the application

### Step 0

- The client must start the application

### Step 1 - Startscreen overview

- Once the application is started, the data from the config files are loaded. There are 2 config files. The first config file stores the url of the server and the second one stores a list with the id's of the recently viewed events by the client (events that the client joined in previous sessions).

- On the startscreen the client is able to do a number of things:
    - The client is able to view the list of events the previously joined on the far right of the page (event title and invitation code).
    - The client is able to create a new event by providing a title for the event on the corresponding input box and clicking on the ```Create``` button.
    - The client is able to join to an existing event by providing the invitation code of the event on the corresponding input box and clicking on the ```Join``` button.
    - Alternatively, The client is able to (re)join an event by double clicking on the event they want to join from their list of events and they will be redirected do the specified event's overview page.

### Step 2 - Event overview

- Once the client is on a specific event's overview page, they can perform a number of actions:
    - The client is able to go back to the startscreen by clicking the big ```home``` button on the top-left of the page.
    - The client is able to edit the title of the event by clicking the small ```pencil``` button, that is located just to the right of the title of the event. If a title is changed by a client, then that change will be reflected both on the event's overview page and on the startscreen for all the clients that are joined to the event.
    - The client is able to copy the invitation code of the event they are currently joined by clicking the big ```Send Invites``` button on the top-right of the page.
    - The client is able to view the list of participants of the event. The participants are displayed in a list format, with each participant's first and last name. Next to each participant there are two buttons:
        * ```Delete``` button. This button allows the client to delete the participant from the event. If a participant is deleted from the event, then that change will be reflected on the event's overview page for all the clients that are joined to the event.
        * ```Edit```  button (See Step 4).
    - The client is able to add a participant to the event by clicking the ```Add Participant``` button on the bottom of the page (See Step 3).
    - The client is able to view the list of expenses of the event. The expenses are displayed in a list format, with each expense's title and amount Next to each expense there are two buttons:
        * ```Delete``` button. This button allows the client to delete the expense from the event. If an expense is deleted from the event, then that change will be reflected on the event's overview page for all the clients that are joined to the event.
        * ```Edit```  button (See Step 6).
    - The client is able to add an expense to the event by clicking the ```Add Expense``` button on the bottom of the page (See Step 5).
    

### Step 3 - Add Participant

- Once the client clicks on the ```Add Participant``` button, teh client will be redirected to the add participant page. The client is able to add a new participant to the event by providing the following fields:
    - First Name
    - Last Name
    - Email
    - IBAN
    - BIC
    - The client is able to add the participant to the event by clicking the ```Add``` button. If the client clicks the ```Add``` button, then the participant will be added to the event and the client will be redirected back to the event's overview page. If the client clicks the ```Abort``` button, then the client will be redirected back to the event's overview page without adding the participant to the event.


### Step 4 - Edit Participant

- Once the client clicks on the ```Edit``` button next to a participant, the client will be redirected to the edit participant page. The client is able to edit the participant's information by providing the following fields:
    - First Name
    - Last Name
    - Email
    - IBAN
    - BIC
    - The client is able to save the changes by clicking the ```Edit``` button. If the client clicks the ```Edit``` button, then the participant's information will be updated and the client will be redirected back to the event's overview page. If the client clicks the ```Abort``` button, then the client will be redirected back to the event's overview page without updating the participant's information.

### Step 5 - Add Expense

- Once the client clicks on the ```Add Expense``` button, the client will be redirected to the add expense page. The client is able to add a new expense to the event by providing the following fields:
    - Title (What for?)
    - Amount (How much?)
    - Who paid? (Select a participant from the list of participants)
    - The client is able to add the expense to the event by clicking the ```Add``` button. If the client clicks the ```Add``` button, then the expense will be added to the event and the client will be redirected back to the event's overview page. If the client clicks the ```Abort``` button, then the client will be redirected back to the event's overview page without adding the expense to the event.

### Step 6 - Edit Expense

- Once the client clicks on the ```Edit``` button next to an expense, the client will be redirected to the edit expense page. The client is able to edit the expense's information by providing the following fields:
    - Title (What for?)
    - Amount (How much?)
    - Who paid? (Select a participant from the list of participants)
    - The client is able to save the changes by clicking the ```Edit``` button. If the client clicks the ```Edit``` button, then the expense's information will be updated and the client will be redirected back to the event's overview page. If the client clicks the ```Abort``` button, then the client will be redirected back to the event's overview page without updating the expense's information.


### Step 7 - Admin panel

- After the client has created an event, the client can download the JSON representation of the created event as a backup.
To do this, the client needs to open the Admin panel.
  - Navigate to the Start Screen by pressing `Escape` on the Event Overview.
  - On the Start Screen, the client can enter the Admin panel by entering the Admin password and pressing the Log in button.
  The admin password can be found in the standard output of the server process. The generated password should look like a UUID of type 4.
  - Once in the Admin panel, the client can JSON dump an event by pressing the blue button with an error pointing downwards next to the corresponding event.
  This will prompt the client with a file dialog to select a destination for the backup.
  - The client can also choose to delete the event by the red button with an 'X' character next to the event the client wishes to delete.
  This action will prompt the client with a confirmation dialog, the event will only be deleted if the client presses the 'OK' button or the `Enter` key.
  - The client can import a saved event by pressing the 'Json Import' button at the bottom. 
  This action will prompt the client with a file selector, the client must then select a valid JSON file containing an event. 
  If the selected JSON file contained a valid event, the admin panel will show the imported event in the list of events.
  - Once the client exits from the Admin panel by pressing the home button or the `Escape` key, the client will be logged out of the Admin panel.

## Present short slides about server-client communication

## Ideas for extending our project

- Add support for tracking the expense statistics of an event.
  - Pie chart that shows the distribution between amount still owed and the amount settled
  - Add support for tags to label the expenses.
  - Pie chart distribution of the expenses per label type.
- Add deadlines for expenses. The date on which all the debts of a single expense should be settled.
  - Create an email reminder system for when the due date is near or past due
- Create an email invite feature.
- Add an email notification system to notify participants when they get a new debt
to settle or when they get money from someone.
- Expand the debt settling mechanism by taking someone straight to a payment provider
where the details of the transaction are automatically filled in. The data for that
could be stored in the config files on the client side.