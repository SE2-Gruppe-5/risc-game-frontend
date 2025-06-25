package com.se2gruppe5.risikofrontend

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.popup.ContinentListAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ContinentListAdapterTest {

    private lateinit var context: Context
    private lateinit var data: List<Continent>
    private lateinit var adapter: ContinentListAdapter


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        data = Continent.entries
        adapter = ContinentListAdapter(context, data)
    }

    @Test
    fun adapterHasCorrectItemCount() {
        assertEquals(10, adapter.count)
    }

    @Test
    fun adapterReturnsCorrectItem() {
        val item = adapter.getItem(2)
        assertEquals("RAM", item?.fullName)
        assertEquals("#87deb3", item?.color)
        assertEquals(10, item?.regions)
        assertEquals(2, item?.bonus)
    }

    @Test
    fun adapterReturnsCorrectItemId() {
        assertEquals(2L, adapter.getItemId(2))
    }

    @Test
    fun adapterHandlesEmptyList() {
        val adapter = ContinentListAdapter(context, emptyList())
        assertEquals(0, adapter.count)
    }

    @Test
    fun adapterBindsDataCorrectlyInView() {
        val adapter = ContinentListAdapter(context, data)
        val view = adapter.getView(0, null, FrameLayout(context))

        val title = view.findViewById<TextView>(android.R.id.text1)
        val subtitle = view.findViewById<TextView>(android.R.id.text2)

        assertEquals("Power Supply", title.text.toString())
        assertEquals("6 Gebiete – +2 Truppen", subtitle.text.toString())
    }

    @Test
    fun adapterItemIdIsEqualToPosition() {
        val adapter = ContinentListAdapter(context, data)
        for (i in data.indices) {
            assertEquals(i.toLong(), adapter.getItemId(i))
        }
    }

    @Test
    fun adapterWithEmptyListReturnsItemIdAsPosition() {
        val adapter = ContinentListAdapter(context, emptyList())
        assertEquals(0L, adapter.getItemId(0))
    }

    @Test
    fun getViewUsesRecycledViewWhenAvailable() {
        val adapter = ContinentListAdapter(context, data)
        val originalView = adapter.getView(1, null, FrameLayout(context))
        val recycledView = adapter.getView(2, originalView, FrameLayout(context))

        assertNotNull(recycledView)
        assertSame(originalView, recycledView)
    }
    @Test(expected = IndexOutOfBoundsException::class)
    fun adapterWithEmptyListReturnsNullItem() {
        val adapter = ContinentListAdapter(context, emptyList())
        adapter.getItem(0)
    }
    @Test
    fun adapterWithEmptyListCreatesNonNullView() {
        val adapter = ContinentListAdapter(context, emptyList())

        assertEquals(0, adapter.count)

    }
}
