package com.devvikram.chatmate.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devvikram.chatmate.AuthViewModel
import com.devvikram.chatmate.AuthViewModelFactory
import com.devvikram.chatmate.ChatActivity
import com.devvikram.chatmate.MyApplication
import com.devvikram.chatmate.UserAdapter
import com.devvikram.chatmate.databinding.FragmentUserBinding
import com.devvikram.chatmate.models.Users
import com.google.firebase.firestore.FirebaseFirestore


private const val ARG_PARAM1 = "param1"

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val fireStore = FirebaseFirestore.getInstance()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var adapter: UserAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appContext = context.applicationContext as MyApplication
        authViewModel =
            ViewModelProvider(this, AuthViewModelFactory(appContext.authRepository)).get(
                AuthViewModel::class.java
            )

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)

        authViewModel.getAllUsers(requireActivity())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerview.layoutManager = layoutManager
        authViewModel.userListLiveData.observe(viewLifecycleOwner) {
            adapter = UserAdapter(it)
            binding.userRecyclerview.adapter = adapter
            adapter.notifyDataSetChanged()

            adapter.setOnItemClickListener(object : UserAdapter.onItemClickListener{
                override fun onItemSelected(users : Users, position: Int) {
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("receiver_name", users.username)
                    intent.putExtra("receiver_email", users.email)
                    intent.putExtra("receiver_id", users._id)
                    startActivity(intent)
                    Log.d(TAG, "onItemSelected: \"Selected User: ${users.email}\"")
                }
            })

        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}