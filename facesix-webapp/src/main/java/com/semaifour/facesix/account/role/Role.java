package com.semaifour.facesix.account.role;

import org.springframework.data.annotation.Id;

import com.semaifour.facesix.domain.FSObject;

public class Role extends FSObject {
	@Id
	private String id;
	private String roleName;

	public Role() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", roleName=" + roleName + "]";
	}

}
