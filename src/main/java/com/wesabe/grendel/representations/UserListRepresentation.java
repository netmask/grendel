package com.wesabe.grendel.representations;

import com.google.common.collect.Lists;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.resources.UserResource;
import org.codehaus.jackson.annotate.JsonGetter;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * A representation of a response containing information about a list of users.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "users":[
 *     {
 *       "uri":"http://example.com/users/codahale",
 *       "id":"codahale"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author coda
 */
public class UserListRepresentation {
	public static class UserListItem {
		private final UriInfo uriInfo;
		private final User user;
		
		public UserListItem(UriInfo uriInfo, User user) {
			this.uriInfo = uriInfo;
			this.user = user;
		}
		
		@JsonGetter("id")
		public String getId() {
			return user.getId();
		}
		
		@JsonGetter("uri")
		public String getUri() {
			return uriInfo.getBaseUriBuilder()
							.path(UserResource.class)
							.build(user).toASCIIString();
		}
	}
	
	private final UriInfo uriInfo;
	private final List<User> users;
	
	public UserListRepresentation(UriInfo uriInfo, List<User> users) {
		this.uriInfo = uriInfo;
		this.users = users;
	}
	
	@JsonGetter("users")
	public List<UserListItem> getUsers() {
		final List<UserListItem> items = newArrayListWithExpectedSize(users.size());
		for (User user : users) {
			items.add(new UserListItem(uriInfo, user));
		}
		return items;
	}

}
