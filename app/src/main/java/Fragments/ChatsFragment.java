package Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import Adapter.UserAdapter;
import Notification.Token;
import P1.Chat;
import P1.User;


public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> musers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<String> userlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userlist = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userlist.clear();
               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                   Chat chat = dataSnapshot1.getValue(Chat.class);
                   assert chat != null;
                   if(chat.getSender().equals(firebaseUser.getUid())&&(!userlist.contains(chat.getReceiver()))){
                   userlist.add(chat.getReceiver());
               }
                   if(chat.getReceiver().equals(firebaseUser.getUid())&&!userlist.contains(chat.getSender())){
                       userlist.add(chat.getSender());
                   }
               chatList();
           }}
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        update(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void update(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token t1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(t1);
    }
    private void chatList(){
        musers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : userlist) {
                        assert user != null;
                        if (user.getId().equals(id)) {
                            musers.add(user);
                        }
                    }
                }
                
                userAdapter = new UserAdapter(getContext(),musers,true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
