# README

This is a Java Project for CUHK-SZ Year-1 Course CSC1004. It takes 60% of all assessment scheme. For more information, please visit [Course Website](https://guiliang.github.io/courses/cuhk-csc-1004/csc_1004.html) and [Project Requirements](https://guiliang.github.io/courses/cuhk-csc-1004/project-topics/chat_room.html).

This Project implements a Java Chatroom that can support multi-user chatting. However, this is just a toy example and poor imitation of a real chatroom and can only run on one's own laptop and currently don't have a cloud server. So it cannot serve any real-world purpose.

All code are open-sourced with as-much-as-possible-user-friendly comments. Most of the code are self-explained.

All code were written in Java. The GUI was written in Java AWT and Java Swing. The knowledge involved also includes Java Socket Programming, JDBC (Java + mySQL), IOStream, multi-thread Programming and so on. Solid Java SE knowledge is also expected in many details like Collection.

The most tricky part in my mind was the IOstream because there're too many of them and I also had to combine them with Socket Programming and Network Knowledge. Some day I spent six hours on the messaging function but finally in vain... It took me around 100-120 hours to finish (including reviewing Java Socket Programming knowledge) this sixty-percent-of-one-unit project.

## Assessment

See [here](https://guiliang.github.io/courses/cuhk-csc-1004/csc-1004-marking-rules.html).

##### Common Grading (20 pts)

- [x] Code Documention 5 pts 
- [x] Video 5 pts
- [x] Tutorial for Running 5 pts
- [x] Can run 5 tps

##### Multi-Client Chat

1. Multi-Client Chat (20pts)
   1. (8pts) A server that could support multiple clients to communicate with each other
   2. (8pts) Could generate multiple clients to work together.
   3. (4pts) The clients could send and receive messages simultaneously. (The basic function of a group chat app works well.)
2. Login System (20pts)
   1. (5pts) Having a database to record user information.
   2. (4pts) The database works well when the login system visits the database for information.
   3. (4pts) A login system that could receive the inputs (e.g., usernames, passwords).
   4. (4pts) The login system could verify the combination of Username and Password.
   5. (3pts) The login system runs smoothly. We could start to chat after logging into the system.
3. Java GUI (20pts)
   1. (10pts) A GUI for the client (5pts) and A GUI for the login System(5pts).
   2. (4pts) Buttons(1pts) and the buttons work well (3pts).
   3. (4pts) Text fields (1pts) and they work well(3pts).
   4. (2pts) The designs are user-friendly.
4. Advanced Features (20pts)
   1. 