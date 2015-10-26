package backend.types;

import com.google.gson.Gson;

/**
 * QuizUser
 *
 * @author dath
 */
public class QuizUser {
	int userID;
	int score;
	String username;
	String apiKey;
	String role;
	String createdAt;

	public QuizUser() {

	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof QuizUser) {
			if(((QuizUser) o).getUserID() != 0 && this.userID != 0)
				return (this.userID == ((QuizUser) o).getUserID() && this.username.equals(((QuizUser) o).getUsername()));
			else
				return (this.username.equals(((QuizUser) o).getUsername()));
		}
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "[username: " + username + ", role: " + role + ", score: "
				+ score + "]";
	}

	public String toJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
