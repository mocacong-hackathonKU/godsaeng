import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mocacong.godsaeng.data.remote.model.response.ErrorResponse
import com.mocacong.godsaeng.widget.utils.ApiState

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {

    private var toast: Toast? = null

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        afterViewCreated()
    }

    protected abstract fun afterViewCreated()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun <T> consumeResponse(
        apiState: ApiState<T>,
        onSuccess: (T) -> (T),
        onError: ((ErrorResponse) -> Unit)? = null,
        onLoading: (() -> Unit)? = null
    ): T? {
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
        return null
    }

    fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(requireContext(), activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

}