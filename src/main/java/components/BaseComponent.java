package components;

import launcher.Builder;

public class BaseComponent {

    Builder builder;

    public BaseComponent (Builder builder)
    {
        this.builder = builder;
    }

    public Builder getBuilder()
    {
        return this.builder;
    }
}
