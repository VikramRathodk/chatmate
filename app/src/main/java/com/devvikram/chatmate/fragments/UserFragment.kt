package com.devvikram.chatmate.fragments

import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        userList.add(Users("Iron Mam",""))
        userList.add(Users("Captain America",""))
        userList.add(Users("Buckey Barnes",""))
        userList.add(Users("Doctor Strange",""))
        userList.add(Users("Thor",""))
        userList.add(Users("Vision",""))
        userList.add(Users("Groot",""))
        userList.add(Users("Drax",""))
        userList.add(Users("Black Panthe",""))
        userList.add(Users("Hawkeye",""))
        userList.add(Users("Spider-Man",""))
        userList.add(Users("Scarlet Witch",""))
        userList.add(Users("Rocket",""))
        userList.add(Users("Bruce Bannner",""))
        userList.add(Users("Falcon",""))
        userList.add(Users("Gamora",""))
        userList.add(Users("Ant-Man",""))

        val layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerview.layoutManager = layoutManager
        val adapter = UserAdapter(userList)
        binding.userRecyclerview.adapter = adapter

       adapter.setOnItemClickListener(object : UserAdapter.onItemClickListener{
           override fun onItemSelected(position: Int) {
               val selectedUser = userList[position]
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