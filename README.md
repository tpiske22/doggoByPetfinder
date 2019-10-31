
proFinal2019
Taylor Piske
MPCS51031 Android Development, University of Chicago
Spring 2019

-------------------

Doggo by PetFinder

Note**
You must sign up for a Petfinder API key and add it to the app's String params for the app to function correctly. I'm keeping my own API key out of the public eye.

This app is meant to help users discover adoptable dogs in their area by querying the PetFinder API.
Upon first launching, you'll be greeted with a welcome message, and if you give the app access to
your current location, the app will initially search for dogs using your current zip code.

The app itself is split into four main screens.

Search
Scroll through results from a dog search. Refresh search listings with the refresh button in the
bottom right. Tap a dog picture to go its detail page, or long press it to bring up a quick-save
prompt, which will let you save the dog to your saved dog list.

Saved
Scroll through dogs you've saved to your saved dog list. Delete all saved dogs with the delete all
button in the bottom right. Tap a dog picture to go to its detail page, or long press it to bring
up a quick-delete prompt, which will let you remove the dog from your saved dog list.

Settings
Set your search filters in the settings section. These include zip code, dog gender, dog size, and
dog age. Press the geolocate button beside the zip code entry box to find your current zip code
automatically. The option to choose between medium and high resolution image downloading is also 
available for data-constrained users.

Dog Detail
By tapping a dog's image, you can view more information about them in the dog detail page. Scroll
through the dog's description, its health information, and more. The section at the bottom is
reserved for shelter/owner information. Their phone number can be called directly from the app by
pressing the Call button, you can view their address in Google Maps with a tap of the Map button,
and if you're in need of more information, you can open the dog's PetFinder webpage by tapping the
Website button, so long as each has been provided. If you wish to add or remove this dog from your 
saved dog list, do so with the add/remove button to the upper right of the dog's picture.



TOKEN RETRIEVAL FAILURE NOTE:
Retrieving a new access token for API calls occasionally fails on the emulator, saying that host
www.petfinder.com can't be found, as if you don't have Internet permissions set or a wifi connection.
Close the emulator and retry if this occurs. This supposedly happens on the emulator if you start 
it, then change networks or sleep your computer.

https://stackoverflow.com/questions/6355498/unable-to-resolve-host-url-here-no-address-associated-with-hostname/24484213
