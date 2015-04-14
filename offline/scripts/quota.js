var ProgressBar = React.createClass({
    render: function () {
        return (
            <progress value={this.props.status} max="100"></progress>
        );
    }
});

var AddSize = React.createClass({
    render: function () {
        return (
            <button onClick={this.props.callBack}>Add 5Mb</button>
        );
    }
});

var IndexedDB = React.createClass({
    addData: function () {
        console.log("Adding Data");
    },
    render: function () {
        return (
            <div className="indexedDBWrapper">
                <ProgressBar status={this.props.size}/>
                <AddSize callBack={this.addData}/>
            </div>
        );
    }
});


var Quota = React.createClass({
    addQuota: function () {
        console.log("Adding Quota");
    },
    render: function () {
        return (
            <div className="quotaWrapper">
                <ProgressBar status={this.props.quota}/>
                <AddSize callBack={this.addQuota}/>
            </div>
        );
    }
});


var Wrapper = React.createClass({
    render: function () {
        return (
            <div className="wrapper">
                <h1>Quota Allocation</h1>
                <Quota quota="0"/>

                <h1>Indexed Db Size</h1>
                <IndexedDB size="0"/>
            </div>
        );
    }
});

React.render(
    <Wrapper/>,
    document.getElementById('content')
);
