# WhatsApp Clone Application Documentation
## !!!!! Work In Progress !!!!!


## Overview

 ### The WhatsApp Clone app has the following main features:

 - **Login and Authentication:** Users can register and log in with their accounts using Firebase Authentication. Passwords and emails are used for authentication.

 - **Tabbed Navigation:** The app uses a ViewPager2 and a TabLayout to allow users to switch between two main tabs: "Conversations" and "Contacts".

 - **Conversation List:** In the "Conversations" tab, users can see a list of recent conversations, including contact names and received messages.

 - **Contact List:** In the "Contacts" tab, users can see a list of contacts, including names and contact information.

 - **User Profile:** Users can access their profile and make edits.

 - **Log Out:** Users can log out of their accounts and return to the login screen.
     

## Architecture

### The WhatsApp Clone app follows a typical Android architecture, with the following main parts:

- **Activities:** The app has multiple activities including login activity, main activity, profile activity and chat activity.

- **Fragments:** Fragments are used to display content in the "Conversations" and "Contacts" tabs of the main activity.

- **Firebase:** Firebase is used for authentication and data storage. Firebase Authentication is used to authenticate users, and Firebase Realtime Database (or Firestore) can be used to store conversation data, contacts, and user information.

- **ViewPager2 and TabLayout:** ViewPager2 is used to create tab navigation between "Conversations" and "Contacts". TabLayout is used to display tabs at the top of the screen.

## Workflow

### Here is a typical workflow for WhatsApp Clone app users:

- Login and Registration:
     Users can register or log in using their email and password.
     After successful login, they are directed to the main screen of the application.

- Main screen:
     On the main screen, users see "Conversations" and "Contacts" tabs.
     They can switch between tabs by tapping on them.

- Conversations:
     In the "Conversations" tab, users can see recent conversations with other contacts.
     They can tap a conversation to open the conversation screen.

- Contacts:
     In the "Contacts" tab, users can see a list of contacts.
     They can tap a contact to start a conversation.

- User Profile:
     Users can access their profile by tapping a profile icon.
     They can edit personal information such as name, profile picture, status, etc.

- Log Out of Account:
     Users can log out of their accounts by tapping "Sign Out" on the profile screen.

## System Requirements

 - Android devices with a supported version of the Android operating system.
 Internet connection for authentication and real-time communication with other users.

## Libraries and Tools

### The WhatsApp Clone app makes use of the following libraries and tools:

- **Firebase Authentication:** For user authentication.
- **Firebase Realtime Database (or Firestore):** For real-time data storage.
- **ViewPager2:** For tabbed browsing.
- **TabLayout:** To display tabs in the user interface.
