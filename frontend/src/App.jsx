
import { Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";

import Products from "./pages/Products";
import ProductDetails from "./pages/ProductDetails";
import CreateProduct from "./pages/CreateProduct";
import EditProduct from "./pages/EditProduct";

import Categories from "./pages/Categories";
import CreateCategory from "./pages/CreateCategory";
import EditCategory from "./pages/EditCategory";
import Favourites from "./pages/Favourites";
import CreateOrder from "./pages/CreateOrder";
import Orders from "./pages/Orders";
import OrderDetails from "./pages/OrderDetails";

import ProtectedRoute from "./components/ProtectedRoute";


function App() {

    return (

        <Routes>

            <Route
                path="/login"
                element={<Login />}
            />


            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <Dashboard />
                    </ProtectedRoute>
                }
            />


            {/* PRODUCT ROUTES */}

            <Route
                path="/products"
                element={
                    <ProtectedRoute>
                        <Products />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/products/create"
                element={
                    <ProtectedRoute>
                        <CreateProduct />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/products/:id/edit"
                element={
                    <ProtectedRoute>
                        <EditProduct />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/products/:id"
                element={
                    <ProtectedRoute>
                        <ProductDetails />
                    </ProtectedRoute>
                }
            />


            {/* CATEGORY ROUTES */}

            <Route
                path="/categories"
                element={
                    <ProtectedRoute>
                        <Categories />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/categories/create"
                element={
                    <ProtectedRoute>
                        <CreateCategory />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/categories/:id/edit"
                element={
                    <ProtectedRoute>
                        <EditCategory />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/favourites"
                element={
                    <ProtectedRoute>
                        <Favourites />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/orders/create"
                element={
                    <ProtectedRoute>
                        <CreateOrder />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/orders"
                element={
                    <ProtectedRoute>
                        <Orders />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/orders/:id"
                element={
                    <ProtectedRoute>
                        <OrderDetails />
                    </ProtectedRoute>
                }
            />

            <Route
                path="*"
                element={<Login />}
            />

        </Routes>
    );
}

export default App;
