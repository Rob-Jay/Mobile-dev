
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

### What would we have done differently
Picking a colour or design scheme for the start would have been helpful as we could of designed each activity as we make them then apply finishng touches at the end instead of redesinging the xml file at the very end.

Start the app at the main activity instead of login so a user can still browse without haveing to create an account, but will have to create on when they decide to purchase.

Using the default login provided by android studio and edited that intead of making one from scratch. It would also be interesting to view other means of loogging in such as fingerprint but this would have ment makeing the the Api of the application Higher.
