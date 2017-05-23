package si.lodrant.chitchat.entities;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StandardResponse {
	private String status;

	public StandardResponse(String status) {
		super();
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == StandardResponse.class) {
			return this.status.equals(((StandardResponse) obj).getStatus());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.status);
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
