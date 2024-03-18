# Mini-Android Contacts Management App

## MainActivity

Main activity responsible for managing the UI and navigation between fragments in the Contacts List app.

### onCreate()

- Initializes the activity when created.
  - Sets content view to "activity_main" layout.
  - Initializes the fragment manager.
  - Adds ContactsFragment to the activity.

### ContactsFragment Interface

#### gotoAddContact()

- Callback method triggered when the user wants to add a new contact.
  - Replaces the current fragment with CreateContactFragment, allowing the user to create a new contact.

#### gotoContactDetails(Contact contact)

- Callback method triggered when the user selects a contact.
  - Replaces the current fragment with DetailsFragment, displaying details of the selected contact.

### CreateContactFragment Interface

#### cancelCreateContact()

- Callback method triggered when the user cancels creating a new contact.
  - Removes the current CreateContactFragment from the back stack, returning to the previous fragment.

#### doneCreateContact()

- Callback method triggered when the user finishes creating a new contact.
  - Removes the current CreateContactFragment from the back stack, returning to the previous fragment.


## ContactsFragment

Fragment responsible for fetching contacts data from an API and populating a RecyclerView.

### onCreateView()

- Inflates the layout for the fragment.
- Returns the root view.

### onViewCreated()

- Sets up the fragment's UI components and functionality.
- Sets the title of the activity to "Contacts".
- Initializes RecyclerView and its adapter.
- Calls getContactsApi() to fetch contacts data.
- Sets OnClickListener for the "Add Contact" button to navigate to CreateContactFragment.

### getContactsApi()

- Makes a GET request to fetch contacts data from the API.
- Parses the JSON response to populate the contacts list.
- Notifies the adapter of data changes to update the RecyclerView.

### postDeleteContactsApi(String cid)

- Makes a POST request to delete a contact using its ID.
- If successful, re-fetches the contacts data to update the list.
- Displays a toast message if the deletion is unsuccessful.

### ContactsAdapter

RecyclerView adapter for displaying contacts.

#### onCreateViewHolder()

- Inflates the layout for each contact list item.

#### onBindViewHolder()

- Binds contact data to the ViewHolder.

#### getItemCount()

- Returns the total number of contacts.

### ContactViewHolder

ViewHolder class for individual contact items.

#### setupUI(Contact contact)

- Binds contact data to the ViewHolder's views.
- Sets OnClickListener for clicking on a contact item to navigate to its details.
- Sets OnClickListener for clicking on the delete icon to delete the contact.

### ContactsListener Interface

Interface for communication with the hosting activity.
- gotoAddContact(): Navigates to the Add Contact screen.
- gotoContactDetails(Contact contact): Navigates to the details screen of a specific contact.


## CreateContactFragment

Fragment responsible for adding a new contact by making a POST request to the API.

### onCreateView()

- Inflates the layout for the fragment.
- Returns the root view.

### onViewCreated()

- Sets up the fragment's UI components and functionality.
- Sets the title of the activity to "Add Contact".
- Sets OnClickListener for the "Cancel" button.
- Sets OnClickListener for the "Submit" button to validate input fields and call createContact().

### createContact(String name, String email, String phone, String type)

- Makes a POST request to create a new contact using the provided details.
- Displays a success message and notifies the hosting activity upon successful creation.
- Displays an error message if the creation is unsuccessful.

### CreateContactListener Interface

Interface for communication with the hosting activity.
- cancelCreateContact(): Notifies the hosting activity to cancel creating a new contact.
- doneCreateContact(): Notifies the hosting activity upon successful creation of a new contact.


## Contact

Represents a contact object retrieved from the API response.

### Constructor

- **Contact(JSONObject jsonObject)**: Constructs a Contact object from a JSON object received from the API response.
  - Parses the JSON object to extract contact details such as ID, name, email, phone, and phone type.

### Serializable

Implements Serializable interface to allow Contact objects to be serialized and passed between components in Android applications.


## DetailsFragment

Represents a fragment to display detailed information about a contact.

### Constructor

- **DetailsFragment()**: Default constructor.

### Static Method

- **newInstance(Contact contact)**: Creates a new instance of DetailsFragment with the provided contact object as an argument.

### Arguments

- **ARG_PARAM_CONTACT**: Key for passing the contact object as an argument to the fragment.

### Lifecycle Methods

- **onCreate(Bundle savedInstanceState)**: Initializes the fragment and retrieves the contact object from the arguments bundle.
- **onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)**: Inflates the layout for the fragment.
- **onViewCreated(View view, Bundle savedInstanceState)**: Called after the fragment's view has been created. Sets up the UI components with the contact details.

### UI Components

- **textViewName**: Displays the contact name.
- **textViewEmail**: Displays the contact email.
- **textViewPhone**: Displays the contact phone number.
- **textViewPhoneType**: Displays the type of phone number (e.g., CELL, HOME, OFFICE).

**All APIs Tested Using Postman**
