import React from 'react';
import ReactDOM from 'react-dom';
import {browserHistory, IndexRoute, Route, Router} from 'react-router';
import App from './components/App';
import Index from './components/Index';
import TalkDetail from './components/TalkDetail';

ReactDOM.render(<Router history={browserHistory}>
    <Route path='/' component={App}>
        <IndexRoute component={Index}/>
        <Route path='/talk/:talkId' component={TalkDetail}/>
        <Route path='/talk/:talkId/:languageCode' component={TalkDetail}/>
    </Route>
</Router>, document.getElementById('root'));
