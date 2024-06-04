package com.devvikram.chatmate.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.ChatActivity
import com.devvikram.chatmate.UserAdapter
import com.devvikram.chatmate.databinding.FragmentUserBinding
import com.devvikram.chatmate.models.Users
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {


    private lateinit var binding: FragmentUserBinding
    private val fireStore = FirebaseFirestore.getInstance()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        val userList = mutableListOf<Users>()
        userList.add(Users("Tony stark","Iron Mam","Tony stark"))
        userList.add(Users("Captain America","","Steve Job"))
        userList.add(Users("Buckey Barnes","","Winter Soldier"))
        userList.add(Users("Doctor Strange","","Steven Strange"))
        userList.add(Users("Thor","", "Thor Odinson"))
        userList.add(Users("Vision","","Vision"))
        userList.add(Users("Groot","","I am Groot"))
        userList.add(Users("Drax","", "Drax the Destroyer"))
        userList.add(Users("Black Panthe","", "Black Panther"))
        userList.add(Users("Hawkeye","","Clint Barton"))
        userList.add(Users("Spider-Man","","Peter Parker"))
        userList.add(Users("Scarlet Witch","","Wanda Maximoff"))
        userList.add(Users("Rocket","","Rocket Raccoon"))
        userList.add(Users("Hulk","", "Bruce Banner"))
        userList.add(Users("Falcon","", "Falcon"))
        userList.add(Users("Gamora","", "Gamora"))
        userList.add(Users("Ant-Man","", "Scott Lang"))

        val layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerview.layoutManager = layoutManager
        val adapter = UserAdapter(userList)
        binding.userRecyclerview.adapter = adapter

       adapter.setOnItemClickListener(object : UserAdapter.onItemClickListener{
           @RequiresApi(Build.VERSION_CODES.R)
           override fun onItemSelected(position: Int) {
               val selectedUser = userList[position]
               val intent = Intent(requireContext(), ChatActivity::class.java)
               intent.putExtra("name",selectedUser.username)
               intent.putExtra("email",selectedUser.email)
               startActivity(intent)
               Log.d(TAG, "onItemSelected: \"Selected User: ${selectedUser.email}\"")
           }
       })

        return binding.root





    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun loadUserData(){


    }


}