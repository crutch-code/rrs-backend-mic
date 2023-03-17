package com.ilyak.entity.jsonviews;

public interface JsonViewCollector {
    interface Default {
    }

    interface WithModerator {
    }

    interface User{
        interface BasicUserView{

        }
        interface WithPassword {
        }

        interface WithAvatarsList{
        }
    }

    interface Flat{
        interface WithFlatOwner{
        }
    }
}
