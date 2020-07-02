import {CLEAR_ALL_ERRORS, SEND_ERROR} from '../constants/error-constants';

const errorReducer = (state = { errors: [] }, action) => {
    switch (action.type) {
        case SEND_ERROR:
            return { ...state, errors: [...state.errors, action.payload] };

        case CLEAR_ALL_ERRORS:
            return { ...state, errors: [] };

        default:
            return state;
    }
};

export default errorReducer;
