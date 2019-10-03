package com.coyoapp.tinytask.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.coyoapp.tinytask.dto.TaskRequest;
import com.coyoapp.tinytask.dto.TaskResponse;
import com.coyoapp.tinytask.exception.TaskNotFoundException;

public class TaskControllerTest extends BaseControllerTest {

	private static final String PATH = "/tasks";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	public void shouldCreateTask() throws Exception {
		// given
		String id = "task-id";
		String name = "task-name";
		MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain",
				"some xml".getBytes());
		TaskRequest taskRequest = TaskRequest.builder().name(name).build();
		TaskResponse taskResponse = TaskResponse.builder().id(id).name(name).build();
		when(taskService.createTask(taskRequest, null)).thenReturn(taskResponse);

		// when/ then
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.multipart(PATH).file(firstFile).param("taskRequest",
				objectMapper.writeValueAsString(taskRequest))).andExpect(status().is(200));

	}

	@Test
	public void shouldGetTasks() throws Exception {
		// given
		String id = "task-id";
		String name = "task-name";
		TaskResponse taskResponse = TaskResponse.builder().id(id).name(name).build();
		when(taskService.getTasks()).thenReturn(Collections.singletonList(taskResponse));

		// when
		ResultActions actualResult = this.mockMvc.perform(get(PATH));

		// then
		actualResult.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(notNullValue()))).andExpect(jsonPath("$[0].name", is(name)));
	}

	@Test
	public void shouldDeleteTask() throws Exception {
		// given
		String id = "task-id";

		// when
		ResultActions actualResult = this.mockMvc.perform(delete(PATH + "/" + id));

		// then
		actualResult.andDo(print()).andExpect(status().isOk());

		verify(taskService).deleteTask(id);
	}

	@Test
	public void shouldNotDeleteTask() throws Exception {
		// given
		String id = "unknown-task-id";
		doThrow(new TaskNotFoundException()).when(taskService).deleteTask(id);

		// when
		ResultActions actualResult = this.mockMvc.perform(delete(PATH + "/" + id));

		// then
		actualResult.andDo(print()).andExpect(status().isNotFound());
	}
}
