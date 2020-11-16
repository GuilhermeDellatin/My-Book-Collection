package br.com.guilhermedellatin.mybookcollection.form

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.model.Book
import kotlinx.android.synthetic.main.fragment_book_form.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BookFormFragment : DialogFragment(), BookFormView {
    val REQUEST_TAKE_PHOTO = 1;
    private val presenter: BookFormPresenter by inject { parametersOf(this) }

    private lateinit var imageView: ImageView
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_form, container, false);
        imageView = view.findViewById(R.id.imgView);
        imageView.setOnClickListener(View.OnClickListener {
            dispatchTakePictureIntent();
        })

        return view;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookId = arguments?.getLong(EXTRA_BOOK_ID, 0) ?: 0
        presenter.loadBook(bookId)
        edtGender.setOnEditorActionListener { _, i, _ ->
            handleKeyboardEvent(i)
        }
        dialog?.setTitle(R.string.action_new_book)
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
    }

    override fun showBook(book: Book) {
        edtName.setText(book.name)
        edtGender.setText(book.gender)
        rtbRating.rating = book.rating
    }

    override fun errorSaveBook() {
        Toast.makeText(requireContext(), R.string.error_book_not_found, Toast.LENGTH_SHORT).show()
    }

    override fun errorInvalidBook() {
        Toast.makeText(requireContext(), R.string.error_invalid_book, Toast.LENGTH_SHORT).show()
    }

    private fun handleKeyboardEvent(actionId: Int): Boolean {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            val book = saveBook()
            if (book != null) {
                if (activity is OnBookSavedListener) {
                    val listener = activity as OnBookSavedListener
                    listener.onBookSaved(book)
                }
                dialog?.dismiss()
                return true
            }
        }
        return false
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(bmp: Bitmap) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        var file: File = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        currentPhotoPath = file.absolutePath
        val fOut = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut)
        fOut.flush()
        fOut.close()
    }

    private fun saveBook(): Book? {
        val book = Book()
        val bookId = arguments?.getLong(EXTRA_BOOK_ID, 0) ?: 0
        book.id = bookId
        book.name = edtName.text.toString()
        book.gender = edtGender.text.toString()
        book.publishing = edtPublishing.text.toString()
        book.rating = rtbRating.rating
        book.path = currentPhotoPath

        if (presenter.saveBook(book)) {
            return book
        } else {
            return null
        }
    }

    fun open(fm: FragmentManager) {
        if (fm.findFragmentByTag(DIALOG_TAG) == null) {
            show(fm, DIALOG_TAG)
        }
    }

    interface OnBookSavedListener {
        fun onBookSaved(book: Book)
    }

    companion object {
        private const val DIALOG_TAG = "edtDialog"
        private const val EXTRA_BOOK_ID = "book_id"

        fun newInstance(bookId: Long = 0) = BookFormFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_BOOK_ID, bookId)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
                createImageFile(imageBitmap)
            } else {
                val book = data?.getSerializableExtra("book") as? Book
                Toast.makeText(activity, book?.name ?: "vazio", Toast.LENGTH_SHORT).show()
            }
        }
    }

}