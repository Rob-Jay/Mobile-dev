
# Technical Design of Application

## Technologies Used

### Firebase Firestore
Firebase Firestore is used to store and retrieve the advertisements created in our application.
### Firebase Storage
We are using Firebase Storage to store and retrieve all advertisement images.
### Firebase Authentication
Firebase Auth is used to allows for the creation and login of our application user accounts.
###FireStore RecyclerView Adapter
We user FireStore RecyclerView Adapter for our searchViewAdapter. This allows ease to impliment and also fast reponsive features for users.
### Google Maps
Each advertisement is given a location. We are using Google Maps to display the advertisement location to other users.
### Stripe Online Payments w/ Volley
The purchasing of advertisements is handled by Stripe Online Payments. We have an external PHP file stored on the UL Hive server. This recieves a request from the application, with the credit card information and advertisement price.
This information is passed to Stripe via Volley and a charge is made against the card. The outcome of the charge request is then sent back to the mobile application and the approperiate steps are taken.

UL Hive URL: http://hive.csis.ul.ie/cs4116/17226864/android-stripe-handler.php

**Volley** is a library which allows HTTP requests to be made from your application to an external server etc.

### Glide
Glide is an image handling library. We are using Glide to help with displaying images from our Firebase Storage database.
