package si.lodrant.chitchat.entities;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CResponse {
	private String status;

	public CResponse(String status) {
		super();
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == CResponse.class) {
			return this.status.equals(((CResponse) obj).getStatus());
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
