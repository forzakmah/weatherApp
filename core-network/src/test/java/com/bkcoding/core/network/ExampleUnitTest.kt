package com.bkcoding.core.network

import com.bkcoding.core.network.httpclient.NetworkResult
import com.bkcoding.core.network.weatherApi.WeatherApiImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    companion object {
        private const val query = "Paris"
        private const val limit = 5
    }

    private lateinit var weatherApi: WeatherApiImpl
    private lateinit var mockWebServer: MockWebServer

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.url("/")
        weatherApi = WeatherApiImpl()
    }

    @Test
    fun test_search_city_200_http_code() = runTest(testDispatcher) {
        val expectedHttpStatusCode = 200

        /**
         * create mocked response
         */
        val expectedResponse = MockResponse()
            .setResponseCode(expectedHttpStatusCode)

        /**
         * enqueue the response
         */
        mockWebServer.enqueue(expectedResponse)

        val currentResponse = weatherApi.searchCity(query, limit)
        if (currentResponse is NetworkResult.Success) {
            assertEquals(currentResponse.code, 200)
        }
    }

    @Test
    fun test_body_search_city_with_success() = runTest(testDispatcher) {
        val expectedHttpStatusCode = 200
        val content = FileTestHelper.readFileResource("cities.json")
        val expectedResponse = MockResponse().also {
            it.setResponseCode(expectedHttpStatusCode)
            it.setBody(content)
        }
        mockWebServer.enqueue(expectedResponse)
        val currentResponse = weatherApi.searchCity(query, limit)
        if (currentResponse is NetworkResult.Success) {
            assertEquals(currentResponse.code, expectedHttpStatusCode)
            assertEquals(currentResponse.data.size, 5)
        }
    }

    @Test
    fun test_error_when_searching_cities() = runTest(testDispatcher) {
        val expectedHttpStatusCode = 500
        val expectedMessage = "Something goes wrong, please try again"
        val content = FileTestHelper.readFileResource("error.json")
        val expectedResponse = MockResponse().also {
            it.setResponseCode(expectedHttpStatusCode)
            it.setBody(content)
        }
        mockWebServer.enqueue(expectedResponse)
        val currentResponse = weatherApi.searchCity(query, limit)
        if (currentResponse is NetworkResult.Error) {
            assertEquals(currentResponse.code, expectedHttpStatusCode)
            assertEquals(currentResponse.message, expectedMessage)
        }
    }

    @Before
    fun tearDown() {
        mockWebServer.shutdown()
    }
}