package com.example.idont

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.idont.databinding.ActivityMainBinding
import com.example.idont.databinding.ActivitySingInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SingInAct : AppCompatActivity() {
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth:FirebaseAuth
    lateinit var binding: ActivitySingInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account!=null){
                    firabaseAuthWithGoogle(account.idToken!!)
                }
            }catch (e: ApiException){
                Log.d("My log", "api exception")
            }
        }
        binding.bSingIn.setOnClickListener {
            signInWithGoogle()
        }
        checkAuth()

    }
    private fun getClient(): GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle(){
        val singInClient = getClient()
        launcher.launch(singInClient.signInIntent)
    }

    private fun firabaseAuthWithGoogle(idToken: String){
        val credencial =GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d("My log ", "Google sign IN ")
                checkAuth()
            }else   {
                Log.d("My log", "Error")
            }
        }
    }

    private fun checkAuth(){
        if(auth.currentUser != null){
         val i = Intent(this, MainActivity::class.java)
         startActivity(i);
        }
    }

}