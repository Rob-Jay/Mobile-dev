# CS4084 - Mobile Application Project

Our application is based on a physical items marketplace, similiar to donedeal or adverts.ie.

## Account Login
Once the application is launched, the user is greeted with an account login screen.
From here the user can choose to login with their account credentials or select the **Create an Account** button.

## Creating your Account
On the *Create your Account* screen, the user is asked to enter their **name**, **email address**, and **password**.
Once your account has been sucessfully created, you will be forwarded to the Home screen. The next time you use the application you can enter your account credentials to login to the application.

## Home Screen
The *home* screen of our application allows the user to **logout**, **view profile details**, **create a new advertisement**, **search advertisements** and **view all recently added advertisements**.

## Logout
Users can logout of their account at any time by clicking the **logout** button on the home screen.

## Create an Advertisement
Once a user has an account, they can create as many new advertisements as they want.

Each advertisement must contain the following details:
- Title
- Description
- Picture
- Quality
- Price
- Location

Once their advertisement has been created it will be displayed on the home screen of the application to all users. It will also become available to search for in the *search* screen.

## Edit an Advertisement
Users can edit their advertisements from their *profile screen*.
A list of their advertisements will be displayed at the top of the page, where they can select the **Edit** button to open the advertisment editor screen.
Once the user saves the required changes they will be instantly updated, and available to all users.

## Search
Users can access the *search screen* from the home screen. They can enter a price range of the items they wish to search for, a search term can also be added.
The search term relates to the advertisements *title*.

If the **search term is left blank, all advertisements** with the matching price range will be returned.

If **no price range is described, all advertisements will be returned**.

After a sucessful search of a product you will be presented with the following details;
- Title
- Description
- Picture
- Quality
- Price
- Location

These details are created by the seller and are associted with the searched product.

## Viewing Advertisements
Advertisements can be viewed by tapping the interested advertisement on the **home screen**, **search screen**, or the **profile screen**.
This will open the advertisement details screen.

## Purchasing Advertisements
Users can purchase an advertisement by clicking the **Purchase** button at the bottom of the **advertisement details screen**.

This will open a Stripe payments screen, where the user will be asked to enter their credit card information. 

In the case that the Stripe payments is unsucessful, there is a backup purchase button underneath the Stripe credit card entry dialog.

# More Information
- [Technical Design](design.md)
- [Navigation Structure](structure.md)
