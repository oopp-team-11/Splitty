# Meeting notes - Week 06

### Attendance
* Everyone was present and on time for the meeting.

### Opening
* We discussed how we handled the midterms.
* Midterms clashed with the workload of some people for the previous week.

### Approval of the agenda
* No more points were added by the participants.

### Announcements by the TA
* No particular announcements were shared by the TA.

---

## Points of action:

### HCI/Accessibility

* Midas said that out of experience JavaFX has good support for some of the accessibility 
and navigation features like the keyboard shortcuts and keyboard navigation. It should be 
relatively easy to implement these features, so introducing them this week before the 
accessibility formative feedback deadline is feasible.
* We agreed that with the current state of our application in terms of server-client communication
via websockets it is unfeasible to add support for undo actions. We also agreed that this feature 
is the most difficult one to introduce in general.
* We agreed to aim for meeting preferably at least 7 rubric items from the accessibility assignment.
It is to make sure that we still pass this part even, if one or two features turn out to be incomplete.
* We agreed to comply with as many accessibility rubrics as possible from now on with every introduced
UI change, as the formative feedback deadline for this assignment is friday week 06. However, with 
websockets still in development, this is not a top priority for this week.
* Implementing all User feedback rubrics (i.e. error messages, informative feedback, confirmations 
on actions) will be possible this or next week after the websocket introduce support for these
features.

### Pacing and planning upcoming weeks
* We are aiming to meet all the basic requirements by the end of week 07.
* Finishing full websocket implementation is the top priority for this week to go towards meeting the
basic requirements.
* The goal for this week is to finish implementing basic requirements for a standard user.
* The next week will be oriented towards implementing an admin panel.
* We started to discuss the language switch feature. Midas volunteered to write the basic support
for it this week.
* We agreed to host an additional meeting right after the official one to plan out the upcoming two
weeks in more detail.

### Distribution of work (discussed after the official meeting)
*TODO: Insert the assigned work here*

### Results of the stand-up
* David created the event data handler on the client side
* Jakub implemented http get requests. Started to implement websockets on the server side.
* Tip from TA: It's typically hard to get a grasp of websockets, so it's good we are doing it already.
* Marios created the event overview scene. Started to fix some of the already implemented
methods in ServerUtils
* Adam configured the websocket connection started implementing support for it on the client side.
* Boaz created basics of websocket controller on the server side. This was later improved by Jakub.
* Midas partially changed the id type in models from Long type to UUID. It required fixing
a lot of cascaded errors. Created a private field accessor utility for testing purposes
to avoid implementing explicit setters in models. Everyone liked the introduction of this feature.


### The current state of the application
* Boaz pointed out that the scene changing doesn't work just yet. Fixing this is one of the
goal of this week. Right now we only have individual scenes working.
* Last week we focused on getting the websockets working instead of fixing already
implemented UI scenes.
* Midas showcased current progress on StartScreen, EditParticipantScreen and EventOverview.
CreateEvent doesn't work properly. Can be implemented using standard REST.


### Discuss Websocket usage and long-polling
* We are on the right track to get the websockets working. 
* We need to add the support to send status codes to specific users.
* We need to start working together to get the websockets working, as the features really
start to interconnect with each other.
* Websockets are the top priority.
* Long polling is left as an unassigned task this week in case
someone has the time to take care of it at the end of the week. It will be handled by the
end of week 07 at the latest.

---

### Questions for the TA
- Q: What do we need to do, if we have failed the buddycheck?
- A: The midterm buddycheck is formative. Failed midterm buddycheck
is not a reason to fail the course.

### Question round
- Q: What to do with insufficient failing pipeline rubric?
- A: We need to run ./gradlew build before pushing our commits to gitlab.
We should also merge main into branch before merging a MR.

### Closing
* No one had plans to work on the project on the same day of the meeting.
* TA suggested to not leave the questions at the end as we prolong the meeting that way.