package sports.facts.bales

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class InitialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SplashTheme)
        setContentView(R.layout.activity_intitial)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
    }
}