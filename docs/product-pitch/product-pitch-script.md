# Product Pitch Script

***

## Requirements from brightspace

### Audience and Goal
The presentation should showcase the final product and highlight features that make the
application unique. The technical level is less relevant, but highlight when you have found great solutions for a
problem. As a metaphor, imagine you would create a product video for an app store that tells potential users what
to expect from your app

### Recommended outline
* Brief introduction (ca. 30s)
* Main Block: What is the state of your application? (ca. 8min)
  * Focus on the user perspective. Do not show code, but implemented features in action. Also, a working application is more interesting than just slides
  * Which features are realized in the UI? How is the interaction flow?
  * Review the grading schema to understand which categories will be assessed. Show features that make the application unique (or go beyond the requirements)
  * If you have cool, but non-visible features, please include them in the presentation as well.
* How would you extend the application, technically or content-wise (ca. 1min)
* Wrap-up (ca. 30s)

### Formal requirements
* The presentations must be 9-10 minutes long, this is a strict requirement.
* There is no required layout/style for the slides content
* The entire team must present (roughly) an equal amount of time, we won't stop the time though.
* The presentation covers all aspects of the recommended outline

***

## Feedback from TA of the pitch draft

### Clarity
* The chosen template of demo slides and script is good, we should keep it
* We need to structure ourselves to have good pacing (e.g., we need to spend more time on complex/interesting features and less time on less interesting/simple features)
* Also in the demo we need to show the application in a day-to-day usage state.
  * Use actual names foe events, like vacation or a party
  * Same holds for participant names and expense titles

### Features
* We should also showcase features in multiple use cases, for example edge cases like invalid data input.
  * And show our unique features that not everyone has.

### Interactions
* The scenes had a nice flow, for the actual presentation emphasize creating a 'storyline' for the app. Walk them through the individual features, in a way that draws the audience in.

***

## Brief introduction and talk about core of project
Hello and welcome to our product pitch about Splitty, the expense splitting app for groups. I, _someone_, will give you today's overview and structure of the pitch.
We'll first give a short introduction to the app and team. Where after we'll talk about the core of the application, 
followed by a quick peak into the server communication. Then we'll give you an impression of the application in a demo, where we'll also explain a bit about the app.
After the demo there is a quick summary of the main usage of the app, and we'll also give some ideas on how to extend the product.
Then at the end, we'll wrap-up this product pitch on Splitty.

***

## Brief side note to live updates using websockets
The important trait of our project is that it is live. We have achieved that by using mostly websockets in our project.
Most of the event endpoints, and all of participant and expense endpoints are implemented via websockets.
That ensures bidirectional communication between the server and the client.

***

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
    - The client can switch server with a provided uri on the bottom left
    - An admin can type in admin password unique for the server and log in to the admin panel in the bottom left.

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
    - IBAN
    - BIC
    - The client is able to add the participant to the event by clicking the ```Add``` button. If the client clicks the ```Add``` button, then the participant will be added to the event and the client will be redirected back to the event's overview page. If the client clicks the ```Abort``` button, then the client will be redirected back to the event's overview page without adding the participant to the event.


### Step 4 - Edit Participant

- Once the client clicks on the ```Edit``` button next to a participant, the client will be redirected to the edit participant page. The client is able to edit the participant's information by providing the following fields:
    - First Name
    - Last Name
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
- Once the admin provides password and clicks the ```Log in``` button, he will be redirected to the admin panel. The admin can see all the events and he can:
    - Delete them,
    - Dump a json of them,
    - Import an event from json
- That way, admin can delete an event, if he pleases to, and can create backups and restore them if necessary.

### Step 8 - Language switch
...

***

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

***

## Wrap-up
Thank you for attending our product pitch. We are very content about the project and proud about the ending result. We are especially satisfied that our app is fully live. Thank you.
