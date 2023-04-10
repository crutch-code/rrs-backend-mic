package com.ilyak.entity.jsonviews;

public interface JsonViewCollector {
    interface Default {

    }

    interface BaseEntity {
        interface Default {

        }
    }

    interface WithModerator {
    }

    interface PushMessage extends BaseEntity{
        interface BasicView extends Default {

        }
    }

    interface Contract extends BaseEntity{
        interface BasicView extends Default {

        }
    }

    interface RentOffer extends BaseEntity{
        interface BasicView extends Default{

        }
        interface WithDates extends BasicView{

        }
        interface OnlyDates{}
    }
    interface Chat extends BaseEntity{
        interface BasicView extends Default {

        }

    }

    interface Message{
        interface BasicView extends BaseEntity.Default {

        }

    }
    interface User{

        interface BasicView extends BaseEntity.Default {
        }
        interface WithPassword extends BasicView {
        }

        interface ForPost extends WithAvatarsList{

        }
        interface WithAvatarsList extends BasicView{
        }
    }


    interface Flat{
        interface BasicView extends BaseEntity.Default {
        }
    }

    interface Post{
        interface BasicView extends BaseEntity.Default {

        }

        interface WithModerator extends BasicView{
        }


    }
}
