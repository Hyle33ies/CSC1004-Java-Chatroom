# Bi-Weekly Report 2023-4-19

#### CSC1004

#### Hua Peichun (122090180)

### Last Updated : 2023-4-19

Java Project : [Java-Chatroom] (https://github.com/Hyle33ies/CSC1004-Java-ChatRoom)

Python Project : [Python-Project] (https://github.com/Hyle33ies/CSC1004-Python-ImageClassification)

## The Project Structure/Features

##### Basics

Multi-Client Chat (20%)

Login System (20%)

Java GUI (20%)

Documentation, Presentation, Code (20%)

##### Advanced Features

Registration System (5%)

Emoji (5%)

Sending Pictures / Videos (10%)

Message History (5%)

## Schedule

**This Schedule was required to be filled on the first report. Due to many factors, this is not rigorously carried out.**

Week 1-2 (2.4-2.17) : 

- Making Learning Plan; 
-  Conceive the main theme and features;
-  Reviewing Java SE; 
- Learning Java Web/Network Basics, MySQL, Maven.

Week 3-4 (2.18-3.3) : 

- Implementing Login & Registration with GUI;
-  Learning more about Java Web/JDBC.

Week 5-7 (3.4-3.24) : Implementing Multi-Client Chat.

Week 7-8 (3.25-4.7) : Implementing Emoji / Sending pictures.

Week 9-10 (4.8 - 4.21) : Implementing Message History; Writing Documentation.

Week 11-12 (4.22 - 4.28) : Finishing Python Project & Java Project Presentation.

## Progress

Java Project: 100% 

Python Project: 100%.

## Week1-2

#### Works finished in the past 2 weeks

1. Java SE Review:
   1. Inheritance
   1. Interfaces, Lambda Expressions, Inner Classes
   1. Exceptions, Assertions
   1. Generics
   1. Collections
2. MySQL Setup & Fundamentals
3. Learning / Recapping Git, Github, Maven, IDEA
4. Learning Web / Networks Basics.

#### Works to be done in the following 2 weeks

1. Learning JDBC, Java Web Basics.
2. Try to implement the Java GUI of login/registration system.

## Week 3-4

#### Works finished in the past 2 weeks

Learning : Java AWT/Swing & SQL basics.

Doing : System register / login system.

I learnt by myself the basic knowledge of Java AWT/Swing. They are quite similar while Swing is more powerful. But the Java AWT can meet most of the requirements in this project.

I also learnt to install MySQL & relevent tools to help me build my database and some basic knowledge of this language. They'll be helpful in sections of register&login system as well as message history function I plan to implement.

I personally think most of the functions in GUI can be implemented by Java AWT. So I just implemented the login & register system using AWT for now. The JDBC part may be implemented in the next two weeks or later for short of time.

#### Works to be done in the following two weeks

Need to learn some basic knowledge of Computer Networks. The multi-client chat part is the most hard and laborious part in this project so I'll invest more time into this project in the following two weeks, after which the mid-term is coming, too. Hopefully I'll first implement the function in command line environment and then move it into GUI. If there's still enough time (I suppose no) I'll implement the JDBC part of the register/login system.

## Week 5-7

#### Works finished in the past 2 weeks

Last three weeks I was preparing for the mid-term examinations and hardly had time to do my project. So I just learn a lot prerequisite to the steps that followed.

The multi-client chat is the hardest and most important part of this project, for which I did spend a lot more time learning Java networking than I'd expected. The multi-client chat mainly entails two core tech: Multitasking and Socket Programming. I've learned multitasking weeks before and mainly learned socket programming along with lecture last week. In my project I temporarily decide to implement chatting using UDP and implement files transmission with TCP/IP, which is more sophisticated.

Along with the Java network learning, I've implemented a toy QQ in command line interface as an exercise and I believe it's easier for me to implement it with Java GUI.

Also, I finish the JDBC but the last step (connecting server, which I don't know how to build a server on my own computer).

I also go through Python Project explanation and find it a little bit hard. I may learn a little Machine/Deep learning after the mid-term week, also in favor of  my DDA2001 assessment scheme.

#### Works to be done in the following two weeks

- Implement Java GUI chatroom basic functions.
- Review Java IO stream in order to better implement information/files transmission.
- Learn more about Java web framework like Springboot.

## Week 8-9

#### Works finished in the past 2 weeks

Loads of work. I'm making up for the progress loss due to mid term and pay lots of time on this project. First, I reviewed the Java IO stream to better prepare for the server side. It was a mistake that I do not include a server at first. So I finished most of the functions of a server and transferred the login & registration function to the server side. I also improve the GUI of the chatroom. Part of the chatting function is also implemented, now client can use a button to get the list of the users connected to the same server. The next step is to finish the chatting function.

Besides, Emoji function which I initially chose may not be implemented because it may be too hard. I'll mainly focus on the other three.

#### Works to be done in the following two weeks

- Finish the chatting function
- Implement the Message History function
- Implement the sending pictures/videos function if possible
- Start writing the tutorial and documentation.



## Week10-11

#### Works finished in the past 2 weeks

Almost everything. I need to spare out enough time for my final exam. So I paid lots of time on those two projects and made tremendous progress.

The Java Project was rather hard in implementing the chatting function, especially when combining the IO Stream and the Socket Programming. Several places need future improvements (See Readme). I also append several extra functions ~~because I'm too joyful after finishing the biggest part~~ (Also see Readme).  The documentation and video tutorial are also uploaded.

The Python Project was much easier for I have previous experience. The Project mainly told us to implement the image Classification. I should import the libraries and training datas, throw them into the mathematical model and plot the pictures of the results. It took me about 4 hours to finish the whole project.