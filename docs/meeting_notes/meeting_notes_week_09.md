# Meeting agenda - Week 09

---

Date:           09.04.2024\
Time:           15:45-16:30\
Location:     DW Cubicle 3 \
Chair:          Adam Szymaniak\
Minute taker:     Boaz Bakhuijzen

---

### Opening (1 min)

Everyone was present and on time.
No personal events needed to be taken into account, besides a sore throat of one of the members.

### Approval of the agenda (1 min)
No additional points were added to the agenda

### Announcements by the TA (5 min)
* The TA said that the Code freeze was moved to sunday midnight instead of friday.
* The TA also reminded us on the knockout-criteria which are still in place this week.
* And reminded us of the Buddy-check, because some miss it through all exams.
* Self-reflection
  - The TA said to use the structure words in the reflection (Situation, task, action etc.)
  - Also, closely look at the requirements given, strictly hold onto these
* Product Pitch:
  - The Application demo can be live or pre-recorded, but not loose pictures on the slides.
  - The talk-over can also be pre-recorded but can be live.

---

## Last week in a nutshell

### Present the last week's work to the TA (app demo) (5 min)
* Finished Admin panel
* Finished Language switch
* Added Pop-ups

### Stand-up (5 min)
* Boaz: Added live-language switch, adjusted UI and added Pop-ups
* Midas: Added server side support for JSON Import and Dump
* David: Added extended expense model with involved model
* Marios: Added a last activity service and added update involved endpoint
* Jakub: Added Polish translation and finished admin panel server utils
* Adam: Did some bug-fixes and adjusted event data handler for extended expense and finished admin panel

---

## Q&A

### Questions for the TA (10-15 min)
- There are no individual parts to the presentation, but everyone needs to at least answer 
  one question during the question round
- For informative feedback in the HCI rubric, we only need to provide one way of giving feedback, we can do multiple
- The way we wanted to implement the expenses should suffice the basic requirements
- We should remove the email address from the database and client, But the IBAN and BIC can stay.
- Tip from TA:
  - We should look at the percentages of how much everything is worth and choose low effort high rewarding tasks. 
  Instead of low rewarding high effort tasks such as testing.


### Plan for the last week (10-15 min)
- Extended expense:
  - still needs some adjustment but should be almost done.
- Involved controller:
  - Endpoints are added, and the whole thing should be finished soon
- Refactoring:
  - The refactoring of multiple constructors and the removal of email address happened after the meeting.
  - And at the time of writing the notes it is already merged
- Pop-ups and accessibility:
  - Pop-ups are almost done, the only need translation(at time of writing these notes, work is already started on it)
  - Accessibility is almost done, only some shortcuts and icons left

### UI work left:

- Accessibility: icons and shortcuts
- Detailed expense overview and Adjusting edit/add expense scene
- Detailed participant information pop-up
- Revamp for event overview to include filtering the expense list.
  See the date and who paid for the expense.


### Question round (2 min)
No questions were left.

### Closing (1 min)
- Closed the meeting, after the meeting we discussed work for next week and divided the work.

## Upcoming week
### Agreements for upcoming week
- We agreed to set a deadline for all code changes, we agreed on Thursday midnight.
- And agreed to have an online meeting for product pitch on Friday.

### Task division
* Boaz: Add translating to pop-ups and adjust event overview for accessibility and extended expense
* Midas: Do some finishing touches on thursday and maybe some code cleaning
* David: Finish models for extended expense and Detailed expense overview controller
* Marios: Finish involved server connection and create detailed expense UI
* Jakub: Add translating to pop-ups and add keyboard shortcuts
* Adam: Adjust add/edit expense UI and controller