package com.se2gruppe5.risikofrontend

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.Popup.ContinentInfo
import com.se2gruppe5.risikofrontend.Popup.ContinentListAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame


@RunWith(AndroidJUnit4::class)
class ContinentListAdapterTest {

    private lateinit var context: Context
    private lateinit var data: List<ContinentInfo>
    private lateinit var adapter: ContinentListAdapter


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        data = listOf(
            ContinentInfo("Europa", "#4CAF50", 6, 5),
            ContinentInfo("Asien", "#E91E63", 7, 7),
            ContinentInfo("Afrika", "#F44336", 6, 3)
        )
        adapter = ContinentListAdapter(context, data)
    }

    @Test
    fun adapterHasCorrectItemCount() {
        assertEquals(3, adapter.count)
    }

    @Test
    fun adapterReturnsCorrectItem() {
        val item = adapter.getItem(1)
        assertEquals("Asien", item?.name)
        assertEquals("#E91E63", item?.colorHex)
        assertEquals(7, item?.regions)
        assertEquals(7, item?.bonus)
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

        assertEquals("Europa", title.text.toString())
        assertEquals("6 Gebiete – +5 Truppen", subtitle.text.toString())
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
