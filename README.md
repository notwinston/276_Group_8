# Summit SFU

**Plan your degree, build your week, and find your way around campus.**

CMPT 276 Group Project Proposal, Group 8

GitHub: https://github.com/notwinston/276_Group_8

Render: https://two76-group-8.onrender.com/

## 1. Project Abstract

Summit SFU is a browser-based application that helps Simon Fraser University students plan their degree, build their weekly timetable, and find their way around campus from a single account. After a student records their program and completed courses, Summit SFU lays out a term-by-term plan toward graduation, recommends courses for the upcoming term based on prerequisites, past offerings, and an AI-generated workload profile of each course, and turns the chosen sections into a conflict-free timetable. Each class links to a room page with the building, floor, directions, and a map, and the application warns students when back-to-back classes are too far apart to reach in time. Between classes, students can search for study spaces by feature, see which classrooms are likely to be open, and share feedback on noise and suitability for group work. Course data comes from the SFU Course Outlines REST API, supported by the TransLink, OpenRouteService, Open-Meteo, and Hugging Face APIs, and the application will be built with Spring Boot and PostgreSQL and deployed on Render. Our goal is to answer three questions every SFU student asks: what to take, when and where their classes are, and where to work in between.

## 2. Name of the Application

Our application is called **Summit SFU**, a reference to SFU's campus at the top of Burnaby Mountain and to reaching graduation one planned step at a time. We moved away from our working title, "SFU Room Finder," because SFU already has an official tool with that name and our scope now goes well beyond finding rooms.

## 3. Understanding the Problem

### 3.1 The Problem

SFU is largely a commuter campus, so students want to make the most of every hour they spend on Burnaby Mountain. Three related problems get in the way.

**Planning a degree is difficult.** Prerequisites are written as free-text sentences in the Academic Calendar, and many upper-division courses are offered only in certain terms, so missing one offering can delay graduation. Sections of the same course can also be graded very differently, with some relying on exams and others on projects, and students have no easy way to compare them.

**Building a weekly schedule is tedious.** Students must avoid time conflicts and long idle gaps while leaving enough time to walk between buildings, and the ten-minute break between classes is often not enough to cross campus.

**Finding places on campus is stressful.** A student may know a building code and room number without knowing where the building is or how to reach the right floor. Students looking for study spaces usually find good ones only through word of mouth.

### 3.2 How the Problem Is Solved Today

Students currently piece together several separate tools. For planning, they read requirements in the Academic Calendar and enrol through goSFU, whose mySchedule feature builds conflict-free timetables and checks prerequisites for the courses in a student's cart. These tools are reliable, but they work one term at a time. They do not lay out a multi-year plan based on when courses are usually offered, compare how sections are graded, or consider how far apart classrooms are. Student-built tools at other universities, such as UWFlow at Waterloo and Berkeleytime at UC Berkeley, offer course reviews and grade data, but neither includes a multi-year prerequisite planner or campus navigation, and neither serves SFU.

For finding places, SFU's interactive Room Finder and campus maps locate rooms and buildings, and the SFU Snap mobile app combines a course schedule with room locations and transit information. The SFU Library booking system lets students reserve library study rooms. However, none of these tools is connected to course planning, none includes student feedback on what a space is like, and the library system covers only bookable library rooms. As a result, a student planning a term may visit six different tools and still have to combine the information in their head.

### 3.3 How Summit SFU Will Make Life Better

Summit SFU brings this information together in one place. A student can see a degree plan, get recommendations that respect prerequisites and offering patterns, compare how sections are graded, and receive a timetable that accounts for walking time between buildings. On campus, the same account holds their saved rooms and favourite study spaces, and feedback from other students helps them choose a space that suits their activity.

Summit SFU is an educational and practical tool rather than an entertainment application, because it helps students make better academic decisions and spend less time searching for information. It complements official SFU systems rather than replacing them. Enrolment and room reservations still happen through SFU's own services, and the application reminds students to confirm their plans with an academic advisor.

### 3.4 Target Audience

Our audience is SFU undergraduate students. New and transfer students will benefit most from room finding and directions, students in the middle of their degree will benefit most from the degree planner and recommendations, and commuters will benefit from compact timetables and transition warnings. A small group of administrators, such as our team or trusted volunteers, will moderate feedback and corrections.

## 4. Project Scope

### 4.1 Use of Web APIs

Our main data source is the **SFU Course Outlines REST API**, a public, read-only API that returns JSON and needs no account or key. Requests follow the pattern `https://www.sfu.ca/bin/wcm/course-outlines?{year}/{term}/{department}/{course}/{section}`, and each section's outline includes its title, description, prerequisites, instructor, grading breakdown, and class and exam schedule. The keywords `current` and `registration` return the active and upcoming terms, and outlines go back to 2014, which gives us about ten years of history to analyze. The schedule lists the campus for each class but not the building or room, so students will record their own rooms, and the application will share those records across users.

A scheduled Spring Boot job will sync the current and upcoming terms into our PostgreSQL database, and a one-time backfill will import past years to build offering histories and grading profiles. Storing the data ourselves keeps pages fast, limits load on SFU's servers, and keeps the application working if the API is down.

Summit SFU will also use four supporting web APIs. The **TransLink GTFS Realtime API**, which requires registering for an API key, provides live bus departure data, and the "today" view will show the next buses leaving SFU's transit stops after a student's last class. The **OpenRouteService API** returns walking routes and travel times between map coordinates, which we will use to estimate how long it takes to walk between buildings. The **Open-Meteo API**, which is free and needs no key, will add the day's forecast so that transition warnings can allow extra time in rain or snow. Finally, the **Hugging Face Hub API** will host the base language model and each version of our fine-tuned workload model, and the application will load the latest version from it, which lets us improve the model without redeploying the application.

### 4.2 Epics

Summit SFU has eight epics that share user accounts and course data, so together they form one connected application.

#### Epic 1: User Accounts, Profiles, and Saved Items

Students will register and log in securely through Spring Security. Each profile stores the student's program, completed courses, and preferences, such as exam-based or project-based assessment, days to avoid, and the longest acceptable gap between classes. Students can save rooms, study spaces, timetables, and degree plans, and administrator accounts unlock moderation tools. This epic delivers the login required for the first iteration.

#### Epic 2: Campus Directory and Interactive Map

Students will search for rooms by building and room number or browse by building and floor. Each room page shows the building, floor, directions from a nearby entrance, accessibility notes, and a location on an interactive map. Each building page lists its rooms, the courses students have placed there, and nearby study spaces. We will seed the directory with SFU's published building list, and students can suggest missing rooms through Epic 4.

#### Epic 3: Study Spaces and Community Feedback

Students will search for study spaces by features such as outlets, whiteboards, capacity, and noise level, and rate spaces for noise, crowding, and comfort at different times of day. When students record the rooms for their classes, the application combines those rooms with the class times from the API to learn when each classroom is in use. It can then show classrooms with no known class at a given time, labelled clearly as unconfirmed because events and private bookings are not included.

#### Epic 4: Corrections and Moderation

Students will report missing or incorrect room details, suggest new study spaces, and flag inappropriate reviews. Administrators will review these reports on a dashboard where they can compare each suggested change with the current record, approve or reject it, and view a full change history. This keeps the directory accurate while protecting it from spam.

#### Epic 5: Course Data Sync and Timetable Builder

This epic owns the jobs that sync the SFU API into our database, including linking each lecture with its tutorials and labs. Students will add the courses they plan to take and generate conflict-free timetables that respect their preferences. When recording rooms, they will see suggestions from other students in the same section. The timetable appears as a weekly calendar and a "today" view, can be exported to a phone calendar, and shows a warning when two classes ten minutes apart are too far to walk between in time, using walking times from OpenRouteService and the forecast from Open-Meteo. The "today" view will also show the next buses leaving campus after the student's last class, using TransLink's real-time data.

#### Epic 6: Degree Planner

The planner models courses and prerequisites as a directed graph. A parser will convert the API's free-text prerequisites into graph edges, and administrators can fix anything it cannot read. Using a topological sort, the planner orders the student's remaining requirements and places each course in a future term when it has historically been offered, producing an editable term-by-term plan. The plan highlights courses that unlock many others, warns about rarely offered courses, and flags any prerequisite broken when the student rearranges it. We will start with the Computing Science major.

#### Epic 7: Course Insights and Recommendations

Using historical outlines, each course will have an insights page showing when it is usually offered, who has taught it, and how its grading is split between exams, assignments, and projects, so students can compare sections taught by different instructors. The recommendation engine will suggest courses for the upcoming term that the student is eligible for, that count toward their degree, and that are being offered. It will rank them by how many future courses they unlock and how well they match the student's preferences, and it will explain each suggestion. Recommendations will also use the AI workload scores from Epic 8.

#### Epic 8: AI Workload Analysis

Each course outline includes a written description of the course, its readings, and its assignments, and an AI model will turn that text into a workload scorecard. We will fine-tune a small open-source language model, such as DistilBERT, on outline text. The training labels will come from the grading weights in the API, plus a smaller set we label by hand, so the model learns to score how exam-heavy, project-heavy, and reading-heavy a course is. The model will be exported to the ONNX format and run either in the student's browser with Transformers.js or on our Spring Boot server with ONNX Runtime, so no paid AI service is needed. The base model will come from Hugging Face, and each trained version will be published to the Hugging Face Hub, where the application loads the latest one. Each course page will show its scorecard, students can compare scores across sections, and the recommendation engine in Epic 7 will use the scores to match courses to each student's preferred workload.

### 4.3 Is the Scope Sufficient for Five Members?

Yes. Eight epics for five members exceeds the guideline of one major feature per person, and each epic includes both back-end work, such as data models, algorithms, and endpoints, and front-end work, such as search pages and interactive views. Several epics also involve real technical challenges, including syncing a large external dataset, generating conflict-free timetables, parsing prerequisites into a graph, ranking recommendations, and training and deploying an AI model. If time runs short, we can postpone smaller features such as live bus times and weather-adjusted warnings without affecting the core application. If we finish early, we can add more programs and the Surrey and Vancouver campuses.

## 5. Team Members and Roles

At our first meeting, we introduced ourselves, discussed our backgrounds, and assigned each member the epics that best match their interests and the ideas they contributed. Every member will also review others' pull requests and help with deployment and documentation.

**Winston** proposed combining room information with saved locations and feedback and expanding the project into degree planning and AI-assisted course recommendations, and will lead the Degree Planner, Course Insights and Recommendations, and AI Workload Analysis. My strongest area is AI/ML. I work in PyTorch, TensorFlow, Keras, scikit-learn, Hugging Face and NLTK. I've done LLM training-data influence research, recommendation systems, reinforcement learning, and classifiers on real data. I'm also comfortable across the full stack. On the frontend I use Svelte/SvelteKit, React, TypeScript, JavaScript, HTML and CSS. On the backend I've worked with Python (Flask), SQL, MongoDB and Supabase. For deployment I use Docker, Kubernetes, AWS, Google Cloud, Cloudflare and Heroku, plus Git, Linux and CI/CD. I also know C++, Java, Rust, Go and ROS 2.

**Jimin** proposed the original room-finding idea and drafted our abstract and problem statement, and will lead the Campus Directory and Interactive Map. Jimin has experience building front-end web apps with HTML, CSS, and JavaScript and using Git and GitHub.

**Habib** proposed saved rooms, study-space filters, student feedback, and corrections, and will lead Study Spaces and Community Feedback. He has experience developing full-stack web applications using Java, Spring Boot, JavaScript, REST APIs, and databases, including building user-facing features for searching, filtering, and managing data.

**Ajay** will lead User Accounts and Corrections and Moderation, and will set up the project structure and Render deployment for the first iteration. He has experience building full-stack web apps with Java, Spring Boot, REST APIs, and SQL databases, including authentication and role-based access with Spring Security. He has also managed team repositories with Git and GitHub and deployed apps to the cloud using Docker.

**Even** will lead the Course Data Sync and Timetable Builder, which supplies the course data used by the other epics. He has experience with relational database design, SQL, JSON, and Java.

## 6. Technology and Development Plan

Summit SFU will be a Spring Boot application written in Java, with Thymeleaf pages, JavaScript and Leaflet for interactive views, Spring Security for login and roles, and Spring Data JPA with PostgreSQL for storage. It will be deployed on Render, and members will work on feature branches merged through reviewed pull requests. The AI workload model will be trained in Python with PyTorch, exported to ONNX, and run with Transformers.js in the browser or ONNX Runtime for Java on the server.

In the first iteration, we will deploy the app with registration, login, and a basic room search. The second iteration will add room pages, saved items, study-space search, and feedback. By the third iteration, the application will sync the SFU API to power timetables, the open-classroom view, and the first version of the degree planner. The final iteration will add course insights, recommendations, the AI workload model, moderation, transition warnings, and live bus times.

## 7. Meeting Notes

Notes from each group meeting will be kept in the folder `meeting-notes` in our repository's root directory and will be submitted with each iteration.
