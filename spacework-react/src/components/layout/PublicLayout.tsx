import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar';

const PublicLayout = () => {
  return (
    <>
      <Navbar />
      <div className="bg-light min-vh-100">
        <Outlet />
      </div>
    </>
  );
};

export default PublicLayout;

