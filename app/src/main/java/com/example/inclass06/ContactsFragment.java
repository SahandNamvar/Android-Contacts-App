package com.example.inclass06;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inclass06.databinding.ContactListItemBinding;
import com.example.inclass06.databinding.FragmentContactsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Contacts Fragment makes a GET request to the contacts/json API and populate the RecyclerView.
public class ContactsFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    ArrayList<Contact> contacts = new ArrayList<>();
    FragmentContactsBinding binding;
    ContactsAdapter adapter;

    public ContactsFragment() {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Contacts");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsAdapter();
        binding.recyclerView.setAdapter(adapter);

        getContactsApi();

        binding.buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoAddContact();
            }
        });
    }

    // GET request to get all contacts
    void getContactsApi() {

        // Create a new request
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contacts/json")
                .build();

        // Async call in child-thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    String body = response.body().string();
                    try {
                        /*
                        The response sent back from API is one whole string - Parse the string and create a JSON object for the whole
                        response - similar to the response object on Postman
                         */
                        JSONObject rootJson = new JSONObject(body);
                        /*
                        Create an array of json objects from the contacts field (key value pair --> contacts: [{contact1}, {contact2}, ...])
                        */
                        JSONArray contactsJsonArray = rootJson.getJSONArray("contacts");
                        contacts.clear(); // Clear list everytime you make a new HTTP GET request

                        for (int i = 0; i < contactsJsonArray.length(); i++) {
                            JSONObject contactJsonObject = contactsJsonArray.getJSONObject(i);
                            Contact contact = new Contact(contactJsonObject);
                            contacts.add(contact);

                        }

                        // TODO: notify adapter data changed
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    Log.d("debug", "onResponse: ");
                }

            }
        });

    }

    // POST request to delete a contact
    void postDeleteContactsApi(String cid) {

        RequestBody formBody = new FormBody.Builder()
                .add("id", cid)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contact/json/delete")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("debug", "onResponseDelete: Successful!");
                    // Re-fetch the api since an item has been deleted - must be done through the Main UI Thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getContactsApi();
                        }
                    });
                } else {
                    Log.d("debug", "onResponseDelete: Unsuccessful!!");
                    String body = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        String message = jsonObject.getString("message");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }

    // RecyclerView Adapter
    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // contact_list_item.xml View Holder Binding
            ContactListItemBinding viewHolderBinding = ContactListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ContactViewHolder(viewHolderBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.setupUI(contact);
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class ContactViewHolder extends RecyclerView.ViewHolder {

            // Set up ViewHolder Binding
            ContactListItemBinding mBinding;
            Contact mContact;

            public ContactViewHolder(ContactListItemBinding viewHolderBinding) {
                super(viewHolderBinding.getRoot());
                mBinding = viewHolderBinding;

                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoContactDetails(mContact);
                    }
                });

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postDeleteContactsApi(mContact.getCid());
                    }
                });
            }

            public void setupUI(Contact contact) {
                this.mContact = contact;
                mBinding.textViewName.setText(mContact.getName());
                mBinding.textViewEmail.setText(mContact.getEmail());
                mBinding.textViewPhone.setText(mContact.getPhone());
                mBinding.textViewPhoneType.setText(mContact.getPhoneType());
            }
        }
    }

    // Interface to Add Contact
    ContactsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ContactsListener) context;
    }

    public interface ContactsListener {
        void gotoAddContact();
        void gotoContactDetails(Contact contact);
    }
}