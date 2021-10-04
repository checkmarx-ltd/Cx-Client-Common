package com.cx.restclient.sast.dto;

public class PostAction {

	private int id;
    private String name;
    private String type;
    private String data;

    public PostAction() {
    }

    public PostAction(int id, String name, String type, String data) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.data = data;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostAction)) return false;

        PostAction postAction = (PostAction) o;

        if (getId() != postAction.getId()) return false;
        return getName().equals(postAction.getName());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
