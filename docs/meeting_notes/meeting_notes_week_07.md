# Meeting notes - Week 06

### Attendance
* Everyone was present and on time for the meeting.

### Opening
* We discussed how everyone experienced the workload
* The TA pointed us to the testing and HCI feedback
* We discussed the presentation time-slot, everyone thinks they will be available on thursday.

### Approval of the agenda
* No more points were added by the participants.

### Announcements by the TA
* No particular announcements were shared by the TA.

---

## Points of action:

### The presentation

The TA told us that during/after the presentation, Ivar will ask us questions concerning both OOPP theory and technical
questions about our program. We should all be informed about all parts of the program, frontend and backend. Ivar 
will likely ask questions about subjects you're less comfortable with. Everyone should answer at least one question.

Make sure not to push our changes last-minute, as the gitlab server could become unreliable as the deadline approaches.

### What counts for test coverage

Not everything is counted for the test coverage, classes like the scene controllers are not 
tested.

### Planning for the upcoming weeks

We should be ready to create the admin frontend, most of the backend stuff should be finished.
Websockets are not expected to still be a problem for this week. We will focuss on fulfilling the basic backlog requirements

### Websockets & Long-polling

For the current implementation of long-polling, we use some API's that are REST-based, we will be replacing these
with websocket communication. This could lead to some problems during the week, we will see.

### What we did this week

* Midas: language switcher system
* Jakub: StatusEntity, fixing the websockets
* Marios: Long-polling and implemented 2 controllers, DeleteEvent and UpdateEvent.
* Adam: fixing the websockets
* David: Worked on integrating the client-side and the server-side
* Boaz: Worked together with David and worked on the expense UI and scene-switching.
  Currently waiting on the websocket stuff before continuing.

---

### Question round
- Q: What is considered a key action?
- A: Any action that modifies or deletes something from the database.
- Q: Can we create a trivial service to reach the backlog requirements, but keep some business logic in the controller
- A: Yes, by the rubric logic, its enough to create one service. It depends on the TA that grades if it is enough of a service.
- Q: In the rubric it says we should test 'exceptional' use of REST endpoints, could you elaborate?
- A: An example of 'exceptional' use would be creating, modifying and getting, as opposed to just getting.
     There are two ways of creating integration tests, one way is using mockito, another way is creating separate entities for tests,
     for example test repositories.
- Q: In the rubric it mentions Guice for dependency injection into the javaFX application, could you elaborate?
- A: I have no knowledge on Guice and you should refer to the backlog.

### Closing
* There will be a meeting in flux after the meeting