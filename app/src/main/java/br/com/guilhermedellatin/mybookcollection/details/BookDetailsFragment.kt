package br.com.guilhermedellatin.mybookcollection.details

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import br.com.guilhermedellatin.mybookcollection.R
import br.com.guilhermedellatin.mybookcollection.form.BookFormFragment
import br.com.guilhermedellatin.mybookcollection.model.Book
import kotlinx.android.synthetic.main.fragment_book_details.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BookDetailsFragment : Fragment(), BookDetailsView {

    private val presenter: BookDetailsPresenter by inject { parametersOf(this) }

    private var book: Book? = null
    private var shareActionProvider : ShareActionProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.book_details, menu)
        val shareItem = menu?.findItem(R.id.action_share)
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as? ShareActionProvider
        setShareIntent()
    }

    private fun setShareIntent() {
        val text = getString(R.string.share_text, book?.name, book?.rating)
        shareActionProvider?.setShareIntent(Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadBookDetails(arguments?.getLong(EXTRA_BOOK_ID, -1) ?: -1)
    }

    override fun showBookDetails(book: Book) {
        this.book = book
        txtName.text = book.name
        txtGender.text = book.gender
        txtPublishing.text = book.publishing
        rtbRating.rating = book.rating
        imgView.setImageBitmap(BitmapFactory.decodeFile(book.path))
    }

    override fun errorBookNotFound() {
        txtName.text = getString(R.string.error_book_not_found)
        txtGender.visibility = View.GONE
        txtPublishing.visibility = View.GONE
        rtbRating.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.action_edit) {
            BookFormFragment.newInstance(book?.id ?: 0).open(requireFragmentManager())
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG_DETAILS = "tagDetalhe"
        private const val EXTRA_BOOK_ID = "bookID"

        fun newInstance(id: Long) = BookDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_BOOK_ID, id)
            }
        }
    }

}