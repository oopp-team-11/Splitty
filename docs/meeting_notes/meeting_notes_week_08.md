# Meeting notes - Week 08

### Attendance

* Everyone was present. Midas was 5 minutes late.

### Opening

* We discussed workload. Deadline for the product pitch draft was stressful, but otherwise it was fine.
* There are no personal events we should take into consideration.
* Everyone thinks they managed to hit the knockout criteria.

### Approval of the agenda

* We did not add any extra points, the agenda was approved.

### Announcements by the TA

* We discussed the formative feedback from the TA. We should set the actual ip in the client-config.
* Do not use ChatGPT for self-reflection. It is fairly easy to spot and will fail you.
* Endterm buddycheck is mandatory and the repair assignment is harsh.

## Points of action:


### Self-Reflection

- Q: How to come up with 2200 words?
- A: Complying with the rubrics is more important than the number of words. If you lack words, try to write more about each situation.
- Q: Can different persons write about the same contribution?
- A: Contributions should be personal, but they may reference the same topic.
- Q: What if we do not write self-reflection?
- A: Self-reflection is mandatory - failing the assignment means you fail the course. There is a repair option, but it is harsh.

### Project rubrics and features

#### Basic requirements not implemented yet
* Admin panel server-side
* Testing manually everything and minor adjustments
* Accessibility (we need at least 5.0; currently we may have or almost have contrast, but nothing else)

#### Advanced features
TA: If the feature is incomplete, but generally usable (general usability might require more than just 1 unimportant bullet point from the list), we may get some partial points.
* Extended expense requires another model - Involved - that contains debtSettled and amountOwned (see schema.pdf). After the official meeting, we debated on this solution, but we settled on keeping it.
* We need to implement pop-ups. We should use messages from status entities.
* Live language switch is a must for us because it is easy to do with our current implementation
* The rest of the advanced features rely on extended expense, so we are currently not able to work on them and most likely we won't
* Multi-modality: we can use emoji for flags for the language selection.
* Except for Midas, everyone has worked on every area of the project, so we can perhaps work according to our strengths to increase efficiency.

#### Discussion

* If we do something on the client side, we should run it with Dutch language as well to check if it fits well
* Scene controllers: Apparently it's not possible to test them, since we can't make instances of some javafx related fields and make gitlab recognize it,
so let's not test it.

### The current state of the application

* We presented the application to the TA. There were no major comments.

### What we did this week

* Adam: admin validation on the server side, login fields for the admin on the start screen, helped with product pitch
* Boaz: scene switching, navigation, product pitch presentation
* David: Admin panel - fxml and controller, adminDataHandler
* Jakub: Admin panel server utils - added frame handlers and expanded websocket session handler to support all admin actions
* Marios: Fixed getting recent events and updating recent events, implemented events:read server endpoint, adjusted password service, implemented admin verification in deleteEvent, recorded an app demo
* Midas: Expanded the translation feature, created the translatable interface

### Distribution of work for this week

* Adam: 
* Boaz: 
* David: 
* Jakub: 
* Marios: 
* Midas: 

### Questions for the TA
- Q: Is just black and white as we have right now counted as contrast?
- A: Yes, it is the safest option. However, use colours for the multi-modality part, keeping in mind it should still be contrasting enough and
take into consideration that medical conditions affecting sight might make it harder to read
- Q: Do beauty and style matter?
- A: No, as long as everything is intuitive. You are not graded for style and resizability - you can just make every UI part large enough
- Q: Do we need to track whether the connection fails?
- A: No.
- Q: Will it be graded that I have not really contributed to the server-side?
- A: Yes, you should work on the server. However, it does not really matter if you, for example, haven't worked on the models, since it is a small part of the project.
- Q: Any recommendations on testing the scene controllers? (We've tried testFx, but gitlab does not run those, and it does not add those to test coverage)
- A: Controllers do count for test coverage, indeed. (After the meeting, at Mattermost): You can generate a test coverage report locally and put it in the repo and link it in the readme

### Closing

* There will be a meeting in Flux afterwards.
* TA: We should not leave important things for the end of the meeting.