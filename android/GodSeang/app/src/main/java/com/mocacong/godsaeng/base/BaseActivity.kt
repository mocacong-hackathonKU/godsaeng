import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.mocacong.godsaeng.data.remote.model.response.ErrorResponse
import com.mocacong.godsaeng.widget.utils.ApiState

abstract class BaseActivity<T : ViewDataBinding, VM : ViewModel>
    (@LayoutRes private val layoutId: Int) : AppCompatActivity() {

    protected lateinit var binding: T
    protected abstract val viewModel: VM
    private var toast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        initView()
        initListener()
    }

    abstract fun initView()
    abstract fun initListener()

    fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun <T> consumeResponse(
        apiState: ApiState<T>,
        onSuccess: (T) -> (T),
        onError: ((ErrorResponse) -> Unit)? = null,
        onLoading: (() -> Unit)? = null
    ) {
        when (apiState) {
            is ApiState.Success -> {
                apiState.data?.let(onSuccess)
            }
            is ApiState.Error -> {
                apiState.errorResponse?.let { errorResponse ->
                    onError?.invoke(errorResponse)
                }
            }
            is ApiState.Loading -> {
                onLoading?.invoke()
            }
        }
    }

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }
}